package name.valery1707.megatel.sorm.api.bro.smtp;

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

	private SpecificationBuilder<BroSmtp, BroSmtpFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<BroSmtp, BroSmtpFilter>(SpecificationMode.AND)
				.withDateTimeDecimal(BroSmtp_.ts, BroSmtpFilter::getTs)
				.withIp(BroSmtp_.idOrigIp, BroSmtpFilter::getIdOrigHost)
				.withIp(BroSmtp_.idRespIp, BroSmtpFilter::getIdRespHost)
				.withNumber(BroSmtp_.idOrigPort, BroSmtpFilter::getIdOrigPort)
				.withNumber(BroSmtp_.idRespPort, BroSmtpFilter::getIdRespPort)
				.withString(BroSmtp_.from, BroSmtpFilter::getFrom)
				.withString(BroSmtp_.to, BroSmtpFilter::getTo)
				.withString(BroSmtp_.subject, BroSmtpFilter::getSubject)
				.withString(BroSmtp_.userAgent, BroSmtpFilter::getUserAgent)
				.withString(BroSmtp_.fuids, BroSmtpFilter::getFuids)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<BroSmtpDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) BroSmtpFilter filter
	) {
		Specification<BroSmtp> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroSmtpDto::new);
	}
}
