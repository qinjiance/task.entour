package com.qinjiance.tourist.task.manager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.qinjiance.tourist.task.constants.ChargeResultEnum;
import com.qinjiance.tourist.task.constants.ChargeStatus;
import com.qinjiance.tourist.task.constants.PayStatus;
import com.qinjiance.tourist.task.manager.exception.TaskManagerException;
import com.qinjiance.tourist.task.model.BillingHotel;
import com.qinjiance.tourist.task.model.vo.ChargeResultVo;
import com.qinjiance.tourist.task.model.vo.GameConnectResponse;

@Component
public class ChargeHotelManager {

	private Logger logger = LoggerFactory.getLogger(ChargeHotelManager.class);

	public static final int CODE_SUSS = 0;

	public ChargeResultVo commonCharge(BillingHotel billingHotel) {
		if (billingHotel == null) {
			logger.error(ChargeResultEnum.ORDER_NOT_FOUND.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.ORDER_NOT_FOUND);
		}
		if (billingHotel.getPayStatus().intValue() != PayStatus.PAYED.getStatus()) {
			logger.error(ChargeResultEnum.ORDER_NOT_PAY.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.ORDER_NOT_PAY);
		}
		if (billingHotel.getChargeStatus().intValue() == ChargeStatus.CHARGED.getStatus()) {
			logger.error(ChargeResultEnum.ORDER_IS_CHARGED.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.ORDER_IS_CHARGED);
		}
			try {
				result = auPayService.thirdCharge(username, billingGateway.getAgent(), CouponManager.AU_COUPON_APPID,
						CouponManager.AU_COUPON_SERVERID, chargeCoupon, billingHotel.getGameRole(), chargeGamePoint,
						chargeScore, billingHotel.getAuOrderId().intValue(), billingHotel.getOrderIp());
			} catch (NumberFormatException e) {
				logger.error("NumberFormatException: ", e);
			} catch (SupportServiceException e) {
				logger.error(e.getMessage());
				errMsg += e.getMessage();
			}
			return result ? new ChargeResultVo(ChargeResultEnum.SUCCESS) : new ChargeResultVo(
					ChargeResultEnum.CONSUME_FAIL.getResultStatus(), errMsg, ChargeResultEnum.CONSUME_FAIL);
	}
}
