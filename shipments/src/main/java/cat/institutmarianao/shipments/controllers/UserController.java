package cat.institutmarianao.shipments.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import cat.institutmarianao.shipments.components.forms.validators.UserFormValidator;
import cat.institutmarianao.shipments.model.User;
import cat.institutmarianao.shipments.model.forms.UserForm;
import cat.institutmarianao.shipments.model.forms.UsersFilter;
import cat.institutmarianao.shipments.services.CompanyService;
import cat.institutmarianao.shipments.services.OfficeService;
import cat.institutmarianao.shipments.services.UserService;
import cat.institutmarianao.shipments.validation.groups.OnUserCreate;
import cat.institutmarianao.shipments.validation.groups.OnUserUpdate;

@Controller
@SessionAttributes({ "user" })
@RequestMapping(value = "/users")
public class UserController {
	@Autowired
	private UserFormValidator userFormValidator;

	@Autowired
	private UserService userService;

	@Autowired
	private OfficeService officeService;

	@Autowired
	private CompanyService companyService;

	@ModelAttribute("user")
	public User setupUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		return (User) userService.loadUserByUsername(username);
	}

	@InitBinder(value = "userForm")
	public void initialiseBinder(WebDataBinder binder) {
		binder.addValidators(userFormValidator);
	}

	@GetMapping("/new")
	public ModelAndView newUser() {

		ModelAndView newUserView = new ModelAndView("user");

		newUserView.getModelMap().addAttribute("offices", officeService.getAllOffices());
		newUserView.getModelMap().addAttribute("companies", companyService.getAllCompanies());
		newUserView.getModelMap().addAttribute("userForm", new UserForm());

		newUserView.getModelMap().addAttribute("edit", false);

		return newUserView;
	}

	@PostMapping("/new")
	public String submitNewUser(@Validated(OnUserCreate.class) UserForm userForm, BindingResult result,
			ModelMap modelMap) {

		if (result.hasErrors()) {
			modelMap.addAttribute("offices", officeService.getAllOffices());
			modelMap.addAttribute("companies", companyService.getAllCompanies());
			return "user";
		}

		userService.add(userForm.getUser());
		return "redirect:/users/list";
	}

	@GetMapping("/edit")
	public ModelAndView editUser(@ModelAttribute("user") User user) {

		ModelAndView editUserView = new ModelAndView("user");

		editUserView.getModelMap().addAttribute("offices", officeService.getAllOffices());
		editUserView.getModelMap().addAttribute("companies", companyService.getAllCompanies());
		editUserView.getModelMap().addAttribute("userForm", new UserForm(user));

		editUserView.getModelMap().addAttribute("edit", true);

		return editUserView;
	}

	@PostMapping("/edit")
	public String submitEditUser(@Validated(OnUserUpdate.class) UserForm userForm, BindingResult result,
			ModelMap modelMap, Model model) {

		if (result.hasErrors()) {
			modelMap.addAttribute("offices", officeService.getAllOffices());
			modelMap.addAttribute("companies", companyService.getAllCompanies());
			modelMap.addAttribute("edit", true);
			return "user";
		}

		if (userForm.getPassword() == null || userForm.getPassword().isBlank()) {
			userForm.setPassword(null);
		}

		User user = userForm.getUser();

		userService.update(user);
		model.addAttribute("user", user);

		return "redirect:/users/list";
	}

	@GetMapping("/list")
	public ModelAndView allUsersList() {

		ModelAndView usersView = new ModelAndView("users");

		usersView.getModelMap().addAttribute("usersFilter", new UsersFilter());
		usersView.getModelMap().addAttribute("users", userService.getAllUsers());

		return usersView;
	}

//	@PostMapping("/ajax/list")
//	@ResponseBody
//	public List<User> filterUserList(@RequestBody UsersFilter requestFilter) {
//		return userService.filterUsers(requestFilter);
//	}

	@GetMapping("/remove/{username}")
	public String removeUser(@PathVariable("username") String username) {

		try {
			userService.remove(username);
		} catch (Exception e) {
			return "redirect:/users/list?removeerror=" + username;
		}
		return "redirect:/users/list";
	}
}
