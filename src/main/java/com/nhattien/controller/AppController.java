package com.nhattien.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nhattien.entities.Order;
import com.nhattien.entities.Product;
import com.nhattien.model.CartInfo;
import com.nhattien.model.ProductInfo;
import com.nhattien.service.OrderService;
import com.nhattien.service.ProductService;
import com.nhattien.untils.Utils;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;

@Controller
public class AppController {

	@Autowired
	private ProductService service;

	@Autowired
	private OrderService serviceOrder;

	@RequestMapping(value = { "/", "/login" })
	public String login() {
		return "login";
	}

	@RequestMapping("/product/search")
	public String search(@RequestParam(defaultValue = "") String name, Model model) {
		model.addAttribute("listProducts", service.search(name));
		return "list_product";
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

	@RequestMapping("/product/new")
	public String showNewProductPage(Model model) {
		Product product = new Product();
		model.addAttribute("product", product);

		return "new_product";
	}

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

	@RequestMapping("/product/edit/{id}")
	public ModelAndView showEditProductPage(@PathVariable(name = "id") int id) {
		ModelAndView mav = new ModelAndView("edit_product");
		Product product = service.get(id);
		mav.addObject("product", product);

		return mav;
	}

	@RequestMapping("/product/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") int id, RedirectAttributes redirect) {
		service.delete(id);
		redirect.addFlashAttribute("success", "Deleted successfully!");
		return "redirect:/product";
	}

	@RequestMapping("/order")
	public String showOrder(Model model) {
		List<Order> orders = serviceOrder.findAll();
		model.addAttribute("listOrders", orders);
		return "list_order";
	}

	@RequestMapping("/addProduct")
	public String listProductHandler(HttpServletRequest request, Model model,
			@RequestParam(value = "id", defaultValue = "") int id) {
		Product product = null;
		if (id != 0) {
			product = service.get(id);
		}
		if (product != null) {
			CartInfo cartInfo = Utils.getCartInSession(request);
			ProductInfo productInfo = new ProductInfo(product);
			cartInfo.removeProduct(productInfo);
		}
		return "redirect:/cart";
	}

	@RequestMapping(value = { "/cart" }, method = RequestMethod.POST)
	public String cartUpdateQty(HttpServletRequest request, Model model,
			@ModelAttribute("cartForm") CartInfo cartForm) {
		CartInfo cartInfo = Utils.getCartInSession(request);
		cartInfo.updateQuantity(cartForm);
		return "redirect:/cart";
	}

	@RequestMapping(value = { "/cart" }, method = RequestMethod.GET)
	public String cartHandler(HttpServletRequest request, Model model) {
		CartInfo myCart = Utils.getCartInSession(request);
		model.addAttribute("cartForm", myCart);
		return "cart";
	}

	@RequestMapping(value = { "/cartConfirmation" }, method = RequestMethod.GET)
	public String cartConfirmationReview(HttpServletRequest request, Model model) {
		CartInfo cartInfo = Utils.getCartInSession(request);

		if (cartInfo == null || cartInfo.isEmpty()) {
			return "redirect:/cart";
		}
		model.addAttribute("myCart", cartInfo);
		return "cartConfirmation";
	}

	@RequestMapping(value = { "/cartConfirmation" }, method = RequestMethod.POST)
	public String cartConfirmationSave(HttpServletRequest request, Model model) {
		CartInfo cartInfo = Utils.getCartInSession(request);

		if (cartInfo.isEmpty()) {
			return "redirect:/cart";
		}

		try {
			serviceOrder.saveOrder(cartInfo);
		} catch (Exception e) {

			return "cartConfirmation";
		}
		// Xóa giỏ hàng khỏi session.
		Utils.removeCartInSession(request);
		Utils.storeLastOrderedCartInSession(request, cartInfo);
		return "redirect:/cartFinalize";
	}

	@RequestMapping(value = { "/cartFinalize" }, method = RequestMethod.GET)
	public String cartFinalize(HttpServletRequest request, Model model) {
		CartInfo lastOrderedCart = Utils.getLastOrderedCartInSession(request);
		if (lastOrderedCart == null) {
			return "redirect:/cart";
		}
		model.addAttribute("lastOrderedCart", lastOrderedCart);
		return "cartFinalize";
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
}
