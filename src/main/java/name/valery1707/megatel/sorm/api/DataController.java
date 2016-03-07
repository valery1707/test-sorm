package name.valery1707.megatel.sorm.api;

import name.valery1707.megatel.sorm.dto.DataDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/data")
public class DataController {
	@Inject
	private DataRepo repo;

	@RequestMapping(method = RequestMethod.GET)
	public Page<DataDto> findAll(
			Pageable pageable,
			@RequestBody(required = false) DataDto filter
	) {
		return repo.findAll(pageable).map(DataDto::fromEntity);
	}
}
