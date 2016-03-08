package name.valery1707.megatel.sorm.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {

	@RequestMapping({"", "index.htm", "index.php"})
	public String index() {
		return "index.html";
	}
}
