package name.valery1707.megatel.sorm.api.bro.smtp;

import name.valery1707.megatel.sorm.app.AccountService;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.BroSmtp;
import name.valery1707.megatel.sorm.domain.BroSmtp_;
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
@RequestMapping("/api/bro/smtp")
public class BroSmtpController {
	@Inject
	private BroSmtpRepo repo;

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<BroSmtp, BroSmtpFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<BroSmtp, BroSmtpFilter>(SpecificationMode.AND)
				.withDateTimeDecimal(BroSmtpFilter::getTs, BroSmtp_.ts)
				.withIp(BroSmtpFilter::getIdOrigHost, BroSmtp_.idOrigIp)
				.withIp(BroSmtpFilter::getIdRespHost, BroSmtp_.idRespIp)
				.withNumber(BroSmtpFilter::getIdOrigPort, BroSmtp_.idOrigPort)
				.withNumber(BroSmtpFilter::getIdRespPort, BroSmtp_.idRespPort)
				.withString(BroSmtpFilter::getFrom, BroSmtp_.from)
				.withString(BroSmtpFilter::getTo, BroSmtp_.to)
				.withString(BroSmtpFilter::getSubject, BroSmtp_.subject)
				.withString(BroSmtpFilter::getUserAgent, BroSmtp_.userAgent)
				.withString(BroSmtpFilter::getFuids, BroSmtp_.fuids)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<BroSmtpDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) BroSmtpFilter filter
	) {
		accountService.requireAnyRight("task.view");
		//todo Фильтрация по выбранной задаче
		Specification<BroSmtp> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroSmtpDto::new);
	}
}
