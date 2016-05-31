package name.valery1707.megatel.sorm.ui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class IndexController {

	@Value("${server.minification.enable}")
	private boolean serveMinFiles = true;

	@RequestMapping({"", "index.htm", "index.php"})
	public ModelAndView index() {
		ModelAndView view = new ModelAndView("index");
		view.addObject("minSuffix", serveMinFiles ? ".min" : "");
		return view;
	}
}
