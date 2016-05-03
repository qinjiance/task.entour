package com.qinjiance.tourist.task.manager;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import module.laohu.commons.util.JsonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qinjiance.tourist.task.constants.ChargeResultEnum;
import com.qinjiance.tourist.task.constants.ChargeStatus;
import com.qinjiance.tourist.task.constants.PayStatus;
import com.qinjiance.tourist.task.model.BillingHotel;
import com.qinjiance.tourist.task.model.vo.BookHotelResult;
import com.qinjiance.tourist.task.model.vo.BookRoomInfo;
import com.qinjiance.tourist.task.model.vo.BookRoomSupplement;
import com.qinjiance.tourist.task.model.vo.ChargeResultVo;
import com.qinjiance.tourist.task.support.tourico.client.TouricoWSClient;
import com.qinjiance.tourist.task.support.tourico.hotel.HotelFlowStub.ArrayOfChildAge;
import com.qinjiance.tourist.task.support.tourico.hotel.HotelFlowStub.ArrayOfRoomReserveInfo;
import com.qinjiance.tourist.task.support.tourico.hotel.HotelFlowStub.ArrayOfSupplementInfo;
import com.qinjiance.tourist.task.support.tourico.hotel.HotelFlowStub.BookV3Request;
import com.qinjiance.tourist.task.support.tourico.hotel.HotelFlowStub.ChildAge;
import com.qinjiance.tourist.task.support.tourico.hotel.HotelFlowStub.ContactPassenger;
import com.qinjiance.tourist.task.support.tourico.hotel.HotelFlowStub.PaymentTypes;
import com.qinjiance.tourist.task.support.tourico.hotel.HotelFlowStub.ResGroup;
import com.qinjiance.tourist.task.support.tourico.hotel.HotelFlowStub.RoomReserveInfo;
import com.qinjiance.tourist.task.support.tourico.hotel.HotelFlowStub.SelectedBoardBase;
import com.qinjiance.tourist.task.support.tourico.hotel.HotelFlowStub.SupplementInfo;

@Component
public class ChargeHotelManager {

	private Logger logger = LoggerFactory.getLogger(ChargeHotelManager.class);

	public static final int CODE_SUSS = 0;

	@Autowired
	private TouricoWSClient touricoWSClient;

	public ChargeResultVo commonCharge(BillingHotel billingHotel) {
		if (billingHotel == null) {
			logger.error(ChargeResultEnum.ORDER_NOT_FOUND.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.ORDER_NOT_FOUND, null);
		}
		if (billingHotel.getPayStatus().intValue() != PayStatus.PAYED.getStatus()) {
			logger.error(ChargeResultEnum.ORDER_NOT_PAY.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.ORDER_NOT_PAY, null);
		}
		if (billingHotel.getChargeStatus().intValue() == ChargeStatus.CHARGED.getStatus()) {
			logger.error(ChargeResultEnum.ORDER_IS_CHARGED.getErrMsg());
			return new ChargeResultVo(ChargeResultEnum.ORDER_IS_CHARGED, null);
		}
		ResGroup result = null;
		String errMsg = null;
		try {
			BookV3Request request = new BookV3Request();
			request.setAgentRefNumber(billingHotel.getId().toString());
			request.setCheckIn(billingHotel.getCheckIn());
			request.setCheckOut(billingHotel.getCheckOut());
			request.setConfirmationEmail(billingHotel.getConfirmationEmail());
			request.setConfirmationLogo(billingHotel.getConfirmationLogo());
			request.setContactInfo(billingHotel.getContactInfo());
			request.setCurrency(billingHotel.getCurrency());
			request.setDeltaPrice(new BigDecimal(billingHotel.getDeltaPrice()));
			request.setHotelId(billingHotel.getHotelId());
			request.setHotelRoomTypeId(billingHotel.getRoomTypeId());
			request.setIsOnlyAvailable(billingHotel.getIsOnlyAvailable());
			request.setRecordLocatorId(0);
			request.setRequestedPrice(new BigDecimal(new DecimalFormat("#.##").format(billingHotel.getPrice()
					/ (double) 100)));
			request.setPaymentType(PaymentTypes.Obligo);

			ArrayOfRoomReserveInfo roomRsvInfos = new ArrayOfRoomReserveInfo();
			RoomReserveInfo roomReserveInfo = null;
			ArrayOfChildAge ages = null;
			ChildAge age = null;
			ContactPassenger contactPassenger = null;
			SelectedBoardBase selectedBoardBase = null;
			ArrayOfSupplementInfo supps = null;
			SupplementInfo supp = null;
			for (BookRoomInfo bookRoomInfo : JsonUtils.parseToList(billingHotel.getRoomInfos(), BookRoomInfo.class)) {
				roomReserveInfo = new RoomReserveInfo();
				roomReserveInfo.setAdultNum(bookRoomInfo.getAdultNum());
				roomReserveInfo.setBedding(bookRoomInfo.getBedding());
				if (bookRoomInfo.getChildAges() != null && !bookRoomInfo.getChildAges().isEmpty()) {
					ages = new ArrayOfChildAge();
					for (Integer childAge : bookRoomInfo.getChildAges()) {
						age = new ChildAge();
						age.setAge(childAge);
						ages.addChildAge(age);
					}
					roomReserveInfo.setChildAges(ages);
				}
				roomReserveInfo.setChildNum(bookRoomInfo.getChildNum());
				roomReserveInfo.setRoomId(bookRoomInfo.getRoomId());
				if (bookRoomInfo.getContactPassenger() != null) {
					contactPassenger = new ContactPassenger();
					contactPassenger.setFirstName(bookRoomInfo.getContactPassenger().getFirstname());
					contactPassenger.setLastName(bookRoomInfo.getContactPassenger().getLastname());
					contactPassenger.setMobilePhone(bookRoomInfo.getContactPassenger().getMobilephone());
					contactPassenger.setHomePhone(bookRoomInfo.getContactPassenger().getHomephone());
					contactPassenger.setMiddleName(bookRoomInfo.getContactPassenger().getMiddlename());
					roomReserveInfo.setContactPassenger(contactPassenger);
				}
				if (bookRoomInfo.getBoadBase() != null) {
					selectedBoardBase = new SelectedBoardBase();
					selectedBoardBase.setId(bookRoomInfo.getBoadBase().getBbId());
					selectedBoardBase.setPrice(new BigDecimal(new DecimalFormat("#.##").format(bookRoomInfo
							.getBoadBase().getPrice() / (double) 100)));
					roomReserveInfo.setSelectedBoardBase(selectedBoardBase);
				}
				if (bookRoomInfo.getSupplements() != null && !bookRoomInfo.getSupplements().isEmpty()) {
					supps = new ArrayOfSupplementInfo();
					for (BookRoomSupplement bookRoomSupplement : bookRoomInfo.getSupplements()) {
						supp = new SupplementInfo();
						supp.setSuppId(bookRoomSupplement.getSuppId());
						supp.setSuppType(bookRoomSupplement.getSuppType());
						supp.setSupTotalPrice(new BigDecimal(new DecimalFormat("#.##").format(bookRoomSupplement
								.getPublishPrice() / (double) 100)));
						supps.addSupplementInfo(supp);
					}
					roomReserveInfo.setSelectedSupplements(supps);
				}
				roomRsvInfos.addRoomReserveInfo(roomReserveInfo);
			}
			request.setRoomsInfo(roomRsvInfos);

			result = touricoWSClient.bookHotel(request);
		} catch (Exception e) {
			logger.error("ChargeHotelManager Exception: ", e);
			errMsg = e.getMessage();
		}
		return (result != null) ? new ChargeResultVo(ChargeResultEnum.SUCCESS, new BookHotelResult(result.getTranNum(),
				result.getRgId())) : new ChargeResultVo(ChargeResultEnum.CONSUME_FAIL.getResultStatus(), errMsg,
				ChargeResultEnum.CONSUME_FAIL, null);
	}
}
