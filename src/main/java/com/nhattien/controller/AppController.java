package com.nhattien.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nhattien.entities.Item;
import com.nhattien.entities.Order;
import com.nhattien.entities.OrderDetail;
import com.nhattien.entities.Product;
import com.nhattien.entities.User;
import com.nhattien.model.AjaxResponseBody;
import com.nhattien.model.SearchCriteria;
import com.nhattien.service.OrderDetailService;
import com.nhattien.service.OrderService;
import com.nhattien.service.ProductService;
import com.nhattien.service.UserService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
public class AppController {

	@Autowired
	private ProductService service;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService serviceOrder;
	
	@Autowired
	private OrderDetailService serviceOrderDetail;

	@RequestMapping(value = { "/", "/login" })
	public String login(@RequestParam(required = false) String message, final Model model) {
		if (message != null && !message.isEmpty()) {
			if (message.equals("logout")) {
				model.addAttribute("message", "You have successfully logged out");
			}
			if (message.equals("error")) {
				model.addAttribute("message", "Username or password is incorrect");
			}
		}
		return "login";
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("register"); // resources/template/register.html
		return modelAndView;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView registerUser(@Valid User user, BindingResult bindingResult, ModelMap modelMap) {
		ModelAndView modelAndView = new ModelAndView();
		// Check for the validations
		if (bindingResult.hasErrors()) {
			modelAndView.addObject("successMessage", "Please correct the errors in form!");
			modelMap.addAttribute("bindingResult", bindingResult);
		} else if (userService.isUserAlreadyPresent(user)) {
			modelAndView.addObject("successMessage", "user already exists!");
		}
		// we will save the user if, no binding errors
		else {
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "User is registered successfully!");
		}
		modelAndView.addObject("user", new User());
		modelAndView.setViewName("redirect:/login");
		return modelAndView;
	}

	@RequestMapping("/users")
	public String showUser(Model model) {
		List<User> users = userService.findAll();
		model.addAttribute("listUsers", users);
		return "list_user";
	}

	@RequestMapping("/product/search")
	@ResponseBody
	public ResponseEntity<?> getSearchResultViaAjax(@Valid @RequestBody SearchCriteria search, Errors errors) {
		AjaxResponseBody result = new AjaxResponseBody();

		// If error, just return a 400 bad request, along with the error message
		if (errors.hasErrors()) {

			result.setMsg(
					errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));

			return ResponseEntity.badRequest().body(result);

		}

		List<Product> products = service.search(search.getName());
		if (products.isEmpty()) {
			result.setMsg("No Product found!");
		} else {
			result.setMsg("success");
		}
		result.setResult(products);

		return ResponseEntity.ok(result);
	}

	@RequestMapping("/product")
	public String showProduct(Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("listProducts", service.findAll(new PageRequest(page, 5)));
		model.addAttribute("currentPage", page);
		return "list_product";
	}

	@RequestMapping("/product/{id}")
	public String viewProduct(Model model, @PathVariable int id) {
		Product product = service.get(id);
		model.addAttribute("pro", product);
		return "view_product";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("/product/new")
	public String showNewProductPage(Model model) {
		Product product = new Product();
		model.addAttribute("product", product);

		return "new_product";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/product/save", method = RequestMethod.POST)
	public String saveProduct(@ModelAttribute("product") Product product, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors()) {
			redirect.addFlashAttribute("error", "Error");
			return "redirect:/product";
		}
		service.save(product);
		redirect.addFlashAttribute("success", "Saved successfully!");
		return "redirect:/product";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("/product/edit/{id}")
	public ModelAndView showEditProductPage(@PathVariable(name = "id") int id) {
		ModelAndView mav = new ModelAndView("edit_product");
		Product product = service.get(id);
		mav.addObject("product", product);

		return mav;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/product/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String deleteProduct(@PathVariable int id) {
		return service.delete(id);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("/order")
	public String showOrder(Model model) {
		List<Order> orders = serviceOrder.findAll();
		model.addAttribute("listOrders", orders);
		return "list_order";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("/orderdetail/{id}")
	public String showOrderDetail(Model model,@PathVariable("id") int id) {
		Order order = serviceOrder.findById(id);
	//	OrderDetail orderDetails = serviceOrderDetail.findByOrder(order);
		List<OrderDetail> orderDetails = serviceOrderDetail.findByOrder(id);
		order.setOrderDetails(orderDetails);
		model.addAttribute("order", order);
	//	model.addAttribute("listOrderDetails", orderDetails);
		return "list_detail";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("/cart")
	public String showCart(Model model, HttpSession session) {
		model.addAttribute("total", sumPrice(session));
		return "cart";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/cart/{id}", method = RequestMethod.GET)
	public String addProduct(@PathVariable(name = "id") int id, Model model, HttpSession session) {
		if (session.getAttribute("cart") == null) {
			List<Item> cart = new ArrayList<Item>();
			cart.add(new Item(service.get(id), 1));
			session.setAttribute("cart", cart);
		} else {
			@SuppressWarnings("unchecked")
			List<Item> cart = (List<Item>) session.getAttribute("cart");
			int index = isExists(id, cart);
			if (index == -1) {
				cart.add(new Item(service.get(id), 1));
			} else {
				int quantity = cart.get(index).getQuantity() + 1;
				cart.get(index).setQuantity(quantity);
			}

		}
		return "redirect:/cart";
	}

	private int isExists(int id, List<Item> cart) {
		for (int i = 0; i < cart.size(); i++) {
			if (cart.get(i).getProduct().getId() == id) {
				return i;
			}
		}
		return -1;
	}
	
	private double sumPrice(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<Item> cart = (List<Item>) session.getAttribute("cart");
		double s = 0;
		if(cart != null) {
			for (Item item: cart) {
				s += item.getQuantity() * item.getProduct().getPrice();
			}
		}
		return s;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/cart/remove/{id}", method = RequestMethod.GET)
	public String removeProduct(@PathVariable(name = "id") int id, HttpSession session) {

		@SuppressWarnings("unchecked")
		List<Item> cart = (List<Item>) session.getAttribute("cart");
		int index = isExists(id, cart);
		cart.remove(index);
		session.setAttribute("cart", cart);
		return "redirect:/cart";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/cart/update", method = RequestMethod.POST)
	public String updateQuantity(HttpSession session, HttpServletRequest request) {
		String[] quantities = request.getParameterValues("quantity");
		@SuppressWarnings("unchecked")
		List<Item> cart = (List<Item>) session.getAttribute("cart");
		for (int i = 0; i < cart.size(); i++) {
			cart.get(i).setQuantity(Integer.parseInt(quantities[i]));
		}
		session.setAttribute("cart", cart);
		return "redirect:/cart";
	}
	
	@RequestMapping(value = "/customer", method = RequestMethod.GET)
	public String customerInfo(ModelMap model) {
		model.put("customer", new Order());
		return "customer";
	}
	
	@RequestMapping(value = "/customer", method = RequestMethod.POST)
	public String saveCustomerAndMakeOrder(HttpSession session, @ModelAttribute("customer") Order order, ModelMap model) {
		order.setDateOrder(new Date());
		order.setAmount(sumPrice(session));
		serviceOrder.create(order);
		@SuppressWarnings("unchecked")
		List<Item> cart = (List<Item>) session.getAttribute("cart");
		for(Item item: cart) {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setAmount(item.getProduct().getPrice());
			orderDetail.setQuantity(item.getQuantity());
			orderDetail.setOrder(order);
			orderDetail.setProduct(item.getProduct());
			serviceOrderDetail.create(orderDetail);
		}
		session.removeAttribute("cart");
		return "redirect:/order";
	}
	
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public void export(HttpServletResponse response) throws IOException, JRException, SQLException {

		response.setContentType("application/x-download");
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"products.pdf\""));
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(service.report());

		InputStream inputStream = this.getClass().getResourceAsStream("/rpt_products.jrxml");
		OutputStream out = response.getOutputStream();
		JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

		/*
		 * HtmlExporter htmlExporter = new
		 * HtmlExporter(DefaultJasperReportsContext.getInstance());
		 * htmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		 * htmlExporter.setExporterOutput(new
		 * SimpleHtmlExporterOutput(response.getWriter())); htmlExporter.exportReport();
		 */
		JasperExportManager.exportReportToPdfStream(jasperPrint, out);
	}

	@RequestMapping("/403")
	public String accessDenied() {
		return "403";
	}
}
