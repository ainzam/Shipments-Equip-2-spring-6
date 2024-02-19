package cat.institutmarianao.shipments.controllers;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import cat.institutmarianao.shipments.model.Assignment;
import cat.institutmarianao.shipments.model.Delivery;
import cat.institutmarianao.shipments.model.Shipment;
import cat.institutmarianao.shipments.model.Shipment.Status;
import cat.institutmarianao.shipments.model.User;
import cat.institutmarianao.shipments.model.forms.ShipmentsFilter;
import cat.institutmarianao.shipments.services.ShipmentService;
import cat.institutmarianao.shipments.services.UserService;

@Controller
@SessionAttributes({ "user" })
@RequestMapping(value = "/shipments")
public class ShipmentController {

	@Autowired
	private UserService userService;

	@Autowired
	private ShipmentService shipmentService;

	@ModelAttribute("user")
	public User setupUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		return (User) userService.loadUserByUsername(username);
	}

    @GetMapping("/new")
    public ModelAndView newShipment(@ModelAttribute("user") User user) {
        ModelAndView modelAndView = new ModelAndView("shipment");
        modelAndView.addObject("shipment", new Shipment());
        return modelAndView;
    }

    @PostMapping("/new")
    public String submitNewShipment(@Validated Shipment shipment, BindingResult result, ModelMap modelMap) {

        if (result.hasErrors()) {
            return "shipment";
        }

        shipmentService.add(shipment);

        modelMap.addAttribute("success", "Envío creado correctamente");
        
        return "redirect:/shipments/list/PENDING";
    }

    @GetMapping("/list/{shipment-status}")
    public ModelAndView allShipmentsList(@ModelAttribute("user") User user,
                                      @PathVariable("shipment-status") Status shipmentStatus) {
    	
        ShipmentsFilter filter = new ShipmentsFilter();
        filter.setStatus(shipmentStatus);
        List<Shipment> shipments = shipmentService.filterShipments(filter);

        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("shipments", shipments);
        model.put("filter", filter);

        return new ModelAndView("shipments", model);
    }

	@PostMapping("/assign")
	public String assignShipment(@Validated Assignment assignment) {

		assignment.setDate(new Date());
		
		shipmentService.tracking(assignment);
		
		return "redirect:/shipments/list/PENDING";
	}

	@PostMapping("/deliver")
	public String deliverShipment(@Validated Delivery delivery) {

		delivery.setDate(new Date());
		
		shipmentService.tracking(delivery);
		
		return "redirect:/shipments/list/IN_PROCESS";
	}
}
