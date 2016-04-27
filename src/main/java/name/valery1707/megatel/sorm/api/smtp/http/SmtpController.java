package name.valery1707.megatel.sorm.api.smtp.http;

import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Smtp;
import name.valery1707.megatel.sorm.domain.Smtp_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@RestController
@RequestMapping("/api/smtp")
public class SmtpController {
	@Inject
	private SmtpRepo repo;

	private SpecificationBuilder<Smtp, SmtpFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<Smtp, SmtpFilter>(SpecificationMode.AND)
//				.withDateTime(Smtp_.ts, SmtpFilter::getTs)
				.withIp(Smtp_.idOrigIp, SmtpFilter::getIdOrigHost)
				.withIp(Smtp_.idRespIp, SmtpFilter::getIdRespHost)
				.withNumber(Smtp_.idOrigPort, SmtpFilter::getIdOrigPort)
				.withNumber(Smtp_.idRespPort, SmtpFilter::getIdRespPort)
				.withString(Smtp_.from, SmtpFilter::getFrom)
				.withString(Smtp_.to, SmtpFilter::getTo)
				.withString(Smtp_.subject, SmtpFilter::getSubject)
				.withString(Smtp_.userAgent, SmtpFilter::getUserAgent)
				.withString(Smtp_.fuids, SmtpFilter::getFuids)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<SmtpDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) SmtpFilter filter
	) {
		Specification<Smtp> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(SmtpDto::new);
	}
}
