package com.qinjiance.tourist.task.manager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qinjiance.tourist.task.constants.ChargeResultEnum;
import com.qinjiance.tourist.task.constants.ChargeStatus;
import com.qinjiance.tourist.task.constants.OrderType;
import com.qinjiance.tourist.task.constants.PayStatus;
import com.qinjiance.tourist.task.manager.exception.TaskManagerException;
import com.qinjiance.tourist.task.mapper.BillingGameConfigMapper;
import com.qinjiance.tourist.task.mapper.BillingGatewayMapper;
import com.qinjiance.tourist.task.model.BillingConfig;
import com.qinjiance.tourist.task.model.BillingGameConfig;
import com.qinjiance.tourist.task.model.BillingGateway;
import com.qinjiance.tourist.task.model.BillingHotel;
import com.qinjiance.tourist.task.model.GameInfo;
import com.qinjiance.tourist.task.model.vo.ChargeResultVo;
import com.qinjiance.tourist.task.model.vo.GameConnectResponse;
import com.qinjiance.tourist.task.support.au.AuPayService;
import com.qinjiance.tourist.task.support.au.AuUserService;
import com.qinjiance.tourist.task.support.exception.SupportServiceException;

@Component
public class ChargeHotelManager {

	private Logger logger = LoggerFactory.getLogger(ChargeHotelManager.class);

	public static final int CODE_SUSS = 0;

	@Autowired
	private AuPayService auPayService;
	@Autowired
	private AuUserService auUserService;
	@Autowired
	private GameServerManager gameServerManager;
	@Autowired
	private BillingConfigManager billingConfigManager;
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private BillingGameConfigMapper billingGameConfigMapper;
	@Autowired
	private BillingGatewayMapper billingGatewayMapper;
	@Autowired
	private GameConnectManager gameConnectManager;

	public ChargeResultVo commonCharge(BillingHotel billingOrder) {
		return commonCharge(billingOrder, false);
	}

	public ChargeResultVo commonCharge(BillingHotel billingOrder, boolean supplement) {
		if (billingOrder == null) {
			logger.error(ChargeResultEnum.ORDER_NOT_FOUND.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.ORDER_NOT_FOUND);
		}
		if (billingOrder.getPayStatus().intValue() != PayStatus.PAYED.getStatus()) {
			logger.error(ChargeResultEnum.ORDER_NOT_PAY.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.ORDER_NOT_PAY);
		}
		if (billingOrder.getChargeStatus().intValue() == ChargeStatus.CHARGED.getStatus()) {
			logger.error(ChargeResultEnum.ORDER_IS_CHARGED.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.ORDER_IS_CHARGED);
		}
		BillingGateway billingGateway = billingGatewayMapper.getByGatewayId(billingOrder.getGatewayId());
		if (billingGateway == null) {
			logger.error(ChargeResultEnum.ORDER_NOT_SUPPORT_GATEWAY.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.ORDER_NOT_SUPPORT_GATEWAY);
		}
		String username = null;
		try {
			username = auUserService.getUsernameByUserId(billingOrder.getToUserId());
		} catch (SupportServiceException e) {
			logger.error(e.getMessage());
		}
		if (StringUtils.isBlank(username)) {
			logger.error(ChargeResultEnum.USER_NOEXIS.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.USER_NOEXIS);
		}
		// 充值完美点券
		Long realChargeAmountYuan = billingOrder.getRealChargeAmount() / 100;
		BillingConfig yuanToCouponRatioConfig = billingConfigManager
				.getBillingConfig(BillingConfigManager.BILLING_CONFIG_KEY_YUAN_TO_COUPON_RATIO);
		int yuanToCouponRatio = yuanToCouponRatioConfig == null ? 100 : Integer.parseInt(yuanToCouponRatioConfig
				.getConfigValue());
		Long chargeCoupon = realChargeAmountYuan * yuanToCouponRatio;
		// 充值游戏点数
		BillingConfig yuanToGamePointRatioConfig = billingConfigManager
				.getBillingConfig(BillingConfigManager.BILLING_CONFIG_KEY_YUAN_TO_GAMEPOINT_RATIO);
		int yuanToGamePointRatio = yuanToGamePointRatioConfig == null ? 9000 : Integer
				.parseInt(yuanToGamePointRatioConfig.getConfigValue());
		Long chargeGamePoint = realChargeAmountYuan * yuanToGamePointRatio;
		// 充值积分
		BillingConfig yuanToScoreRatioConfig = billingConfigManager
				.getBillingConfig(BillingConfigManager.BILLING_CONFIG_KEY_YUAN_TO_SCORE_RATIO);
		int yuanToScoreRatio = yuanToScoreRatioConfig == null ? 15 : Integer.parseInt(yuanToScoreRatioConfig
				.getConfigValue());
		Long chargeScore = realChargeAmountYuan * yuanToScoreRatio;
		// 检查订单类型
		OrderType orderType = OrderType.getEnum(billingOrder.getOrderType());
		orderType = orderType == null ? OrderType.GAME : orderType;
		boolean result = false;
		String errMsg = "充值通知结果：";
		switch (orderType) {
		case COUPON:
			try {
				result = auPayService.thirdCharge(username, billingGateway.getAgent(), CouponManager.AU_COUPON_APPID,
						CouponManager.AU_COUPON_SERVERID, chargeCoupon, billingOrder.getGameRole(), chargeGamePoint,
						chargeScore, billingOrder.getAuOrderId().intValue(), billingOrder.getOrderIp());
			} catch (NumberFormatException e) {
				logger.error("NumberFormatException: ", e);
			} catch (SupportServiceException e) {
				logger.error(e.getMessage());
				errMsg += e.getMessage();
			}
			return result ? new ChargeResultVo(ChargeResultEnum.SUCCESS) : new ChargeResultVo(
					ChargeResultEnum.CONSUME_FAIL.getResultStatus(), errMsg, ChargeResultEnum.CONSUME_FAIL);
		case GAME:
			GameInfo gameInfo = gameServerManager.getByGameId(billingOrder.getGameId());
			if (gameInfo == null) {
				logger.error(ChargeResultEnum.ORDER_NOT_SUPPORT_GAME.getErrMsg());
				return new ChargeResultVo(ChargeResultEnum.ORDER_NOT_SUPPORT_GAME);
			}
			BillingGameConfig billingGameConfig = billingGameConfigMapper.getByGameId(billingOrder.getGameId());
			if (billingGameConfig == null || !billingGameConfig.getIsopen().booleanValue()) {
				logger.error(ChargeResultEnum.ORDER_IS_NOT_OPEN.getErrMsg());
				return new ChargeResultVo(ChargeResultEnum.ORDER_IS_NOT_OPEN);
			}
			// 检查是否为完美游戏
			try {
				// 无冬使用AU充值
				if (gameServerManager.isAUGame(gameInfo) || gameInfo.getId() == 200007L) {
					result = auPayService.thirdCharge(username, billingGateway.getAgent(), Integer.valueOf(gameInfo
							.getThirdGameId()), billingOrder.getServerId(), chargeCoupon, billingOrder.getGameRole(),
							chargeGamePoint, chargeScore, billingOrder.getAuOrderId().intValue(), billingOrder
									.getOrderIp());
				} else {
					Long gameOrderUserId = billingOrder.getToUserId();
					// 173的游戏需要173UserId
					if (gameServerManager.is173Game(gameInfo)) {
						gameOrderUserId = billingOrder.getTo173UserId();
					}

					GameConnectResponse<String> res = null;
					res = gameConnectManager.notifyCharge(billingOrder.getGameId(), gameOrderUserId,
							billingOrder.getRealPayAmount(), billingOrder.getPayTypeName(),
							billingOrder.getRealChargeAmount(), billingOrder.getGameOrderId(), billingOrder.getId(),
							billingOrder.getServerId(), billingOrder.getGameExtraInfo(),
							String.valueOf(PayStatus.PAYED.getStatus()));
					if (res != null) {
						errMsg += res.getMessage();
					} else {
						errMsg += "游戏服务器响应为空";
					}
					result = (res != null && res.getCode() == CODE_SUSS) ? true : false;
				}
			} catch (TaskManagerException e) {
				logger.error(e.getMessage());
			} catch (NumberFormatException e) {
				logger.error("NumberFormatException: ", e);
			} catch (SupportServiceException e) {
				logger.error(e.getMessage());
				errMsg += e.getMessage();
			}
			return result ? new ChargeResultVo(ChargeResultEnum.SUCCESS) : new ChargeResultVo(
					ChargeResultEnum.CONSUME_FAIL.getResultStatus(), errMsg, ChargeResultEnum.CONSUME_FAIL);
		default:
			logger.error(ChargeResultEnum.ORDER_NOT_SUPPORT_ORDER_TYPE.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.ORDER_NOT_SUPPORT_ORDER_TYPE);
		}
	}
}
