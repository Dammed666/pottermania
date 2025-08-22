package com.pottermania.pottermania.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap; // <-- NUEVA IMPORTACIÓN
import java.util.List;
import java.util.Map;
import java.util.Optional; 

import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import com.pottermania.pottermania.Articulo;
import com.pottermania.pottermania.ArticuloRepository;
import com.pottermania.pottermania.Cliente;
import com.pottermania.pottermania.ClienteRepository;
import com.pottermania.pottermania.Venta;
import com.pottermania.pottermania.VentaDetalle;
import com.pottermania.pottermania.VentaDetalleRepository;
import com.pottermania.pottermania.VentaRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/carrito")
public class CartController {

    private static final double IVA_RATE = 0.21;
    
    private final ArticuloRepository articuloRepository;
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final VentaDetalleRepository ventaDetalleRepository;
    
    // FIX: Se usa @Autowired para la inyección de dependencias en el constructor
    //@Autowired
    public CartController(ArticuloRepository articuloRepository, ClienteRepository clienteRepository, VentaRepository ventaRepository, VentaDetalleRepository ventaDetalleRepository) {
        this.articuloRepository = articuloRepository;
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
        this.ventaDetalleRepository = ventaDetalleRepository;
    }

    
    @GetMapping("")
    public String showCart(HttpSession session, Model model) {
        Map<Long, Integer> cart = getCartFromSession(session);
        
        // Cambia a LinkedHashMap para mantener el orden
        Map<Articulo, Integer> articulosEnCarrito = new LinkedHashMap<>();
        double subtotal = 0.0;

        List<Cliente> clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);
        
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Optional<Articulo> articuloOptional = articuloRepository.findById(entry.getKey());
            if (articuloOptional.isPresent()) {
                Articulo articulo = articuloOptional.get();
                articulosEnCarrito.put(articulo, entry.getValue());
                subtotal += articulo.getPrecio() * entry.getValue();
            }
        }
        
        double iva = subtotal * IVA_RATE;
        double total = subtotal + iva;
        
        model.addAttribute("articulosEnCarrito", articulosEnCarrito);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("iva", iva);
        model.addAttribute("total", total);
        model.addAttribute("titulo", "Carrito - Pottermania");
        
        return "carrito";
    }

    // FIX: La URL es "/add/{id}" para que la ruta sea "/carrito/add/{id}"
    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, @RequestParam(defaultValue = "1") int cantidad,
                        @RequestParam(required = false) Long categoriaId, // AÑADIDO
                        HttpSession session, RedirectAttributes redirectAttributes) {
        Map<Long, Integer> cart = getCartFromSession(session);
        
        cart.put(id, cart.getOrDefault(id, 0) + cantidad);
        session.setAttribute("cart", cart);
        
        redirectAttributes.addFlashAttribute("mensajeExito", "Artículo añadido al carrito con éxito.");
        if (categoriaId != null) {
                return "redirect:/tienda/categoria/" + categoriaId;
            } else {
                return "redirect:/tienda"; // Redirección por defecto a la página principal de la tienda
            }
    }

    // FIX: La URL es "/remove/{id}" para que la ruta sea "/carrito/remove/{id}"
    @GetMapping("/eliminar/{id}")
    public String removeCartItem(@PathVariable("id") Long articuloId, HttpSession session) {
        Map<Long, Integer> cart = getCartFromSession(session);

        if (cart != null) {
            cart.remove(articuloId);
        }
        return "redirect:/carrito";
    }
    
    // =========================================================================
    // NUEVO MÉTODO PARA ACTUALIZAR CANTIDAD (añadir o quitar)
    // =========================================================================
    @GetMapping("/actualizar-cantidad/{id}")
    @ResponseBody
    public Map<String, Object> actualizarCantidad(@PathVariable("id") Long articuloId,
                                                   @RequestParam("action") String action,
                                                   HttpSession session) {
        Map<Long, Integer> cart = getCartFromSession(session);

        // FIX: Añade una comprobación de nulidad para evitar el NullPointerException
        if (cart == null) {
            // En caso de que el carrito sea nulo, devuelve una respuesta vacía o un error
            // para que el JavaScript lo maneje.
            return new HashMap<>(); 
        }
        
        if (cart.containsKey(articuloId)) {
            Integer cantidadActual = cart.get(articuloId);
            if ("add".equals(action)) {
                cart.put(articuloId, cantidadActual + 1);
            } else if ("remove".equals(action) && cantidadActual > 1) {
                cart.put(articuloId, cantidadActual - 1);
            }
        }
        
        // Recalcular los totales del carrito
        double subtotal = 0.0;
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Optional<Articulo> articuloOptional = articuloRepository.findById(entry.getKey());
            if (articuloOptional.isPresent()) {
                subtotal += articuloOptional.get().getPrecio() * entry.getValue();
            }
        }
        
        double iva = subtotal * IVA_RATE;
        double total = subtotal + iva;

        // Crear el mapa con los nuevos valores para devolver en formato JSON
        Map<String, Object> response = new HashMap<>();
        response.put("nuevaCantidad", cart.get(articuloId));
        
        // Utiliza BigDecimal para asegurar una correcta precisión al redondear
        response.put("subtotal", new BigDecimal(subtotal).setScale(2, RoundingMode.HALF_UP));
        response.put("iva", new BigDecimal(iva).setScale(2, RoundingMode.HALF_UP));
        response.put("total", new BigDecimal(total).setScale(2, RoundingMode.HALF_UP));

        return response;
    }
    // =========================================================================

    // FIX: La URL es "/finalizar-compra" para que la ruta sea "/carrito/finalizar-compra"
    @PostMapping("/finalizar-compra")
    @Transactional
    public String finalizarCompra(@RequestParam String nombre,
                                @RequestParam String email,
                                @RequestParam(defaultValue = "false") boolean contrareembolso,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        // Obtener el carrito de la sesión
        Map<Long, Integer> cart = getCartFromSession(session);
        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", "El carrito está vacío. No se puede finalizar la compra.");
            return "redirect:/carrito";
        }

        // guardar o encontrar cliente
        Cliente cliente = clienteRepository.findByEmail(email).orElseGet(() -> {
            Cliente newCliente = new Cliente();
            newCliente.setNombre(nombre);
            newCliente.setEmail(email);
            return clienteRepository.save(newCliente);
        });

        // AÑADE ESTA COMPROBACIÓN CRÍTICA
        if (cliente == null || cliente.getId() == null) {
            System.err.println("ERROR: El objeto cliente es nulo o no tiene ID después de intentar guardarlo.");
            redirectAttributes.addFlashAttribute("mensajeError", "Ocurrió un error al procesar el cliente. Por favor, inténtelo de nuevo.");
            return "redirect:/carrito";
        }

        // Calcular los importes totales
        double subtotal = 0.0;
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Optional<Articulo> articuloOptional = articuloRepository.findById(entry.getKey());
            if (articuloOptional.isPresent()) {
                subtotal += articuloOptional.get().getPrecio() * entry.getValue();
            }
        }
        double iva = subtotal * 0.21;
        double total = subtotal + iva;

        // Guardar la venta principal
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFechaVenta(LocalDate.now());
        venta.setImpuestoIva(iva);
        venta.setImporteTotal(total);
        Venta savedVenta = ventaRepository.save(venta);

        // Recorrer el carrito y guardar cada VentaDetalle
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Optional<Articulo> articuloOptional = articuloRepository.findById(entry.getKey());

            if (articuloOptional.isPresent()) {
                Articulo articulo = articuloOptional.get();

                VentaDetalle detalle = new VentaDetalle();
                detalle.setVenta(savedVenta);
                detalle.setArticulo(articulo);
                detalle.setCantidad(entry.getValue());
                detalle.setPrecioUnitario(articulo.getPrecio());

                ventaDetalleRepository.save(detalle);
            } else {
                System.err.println("ERROR: Artículo con ID " + entry.getKey() + " no encontrado en la base de datos.");
            }
        }

        // Vaciar el carrito de la sesion
        session.removeAttribute("cart");

        // Mensaje de exito y redireccion
        redirectAttributes.addFlashAttribute("mensajeExito", "¡Compra finalizada con éxito! Recibirás un correo de confirmación.");
        return "redirect:/tienda";
        
    }


    
    // Método auxiliar
    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCartFromSession(HttpSession session) {
        Object cartAttribute = session.getAttribute("cart");
        if (cartAttribute == null) {
            // Si no hay carrito, crea uno nuevo
            return new LinkedHashMap<>(); // <-- Cambia a LinkedHashMap
        }
        
        // Si el objeto no es un mapa válido, crea uno nuevo para evitar errores
        if (!(cartAttribute instanceof Map)) {
            return new HashMap<>();
        }
        
        // Intenta castear el mapa de forma segura
        try {
            return (Map<Long, Integer>) cartAttribute;
        } catch (ClassCastException e) {
            // Si falla el cast, devuelve un mapa vacío
            return new HashMap<>();
        }
    }
}