package com.qinjiance.tourist.task.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.qinjiance.tourist.task.model.BillingHotel;

public interface BillingHotelMapper {
	@Select("SELECT * FROM billing_hotel WHERE pay_status=#{payStatus}")
	public List<BillingHotel> getAllOrderByPayStatus(@Param("payStatus") Integer payStatus);

	@Select("SELECT * FROM billing_hotel WHERE pay_status = 1 AND charge_status = 2 ORDER BY create_time")
	public List<BillingHotel> getProcessingList();

	@Select("SELECT * FROM billing_hotel WHERE pay_status = 1 AND charge_status = 0 ORDER BY create_time limit #{limit}")
	public List<BillingHotel> getUntreatedList(@Param("limit") int limit);

	@Update("UPDATE billing_hotel SET charge_status = #{chargeStatus}, charge_failed_info = #{failedInfo}, charge_time = now() WHERE id = #{orderId} and pay_status = 1 and charge_status != 1")
	public int updateChargeOrder(@Param("orderId") long orderId, @Param("chargeStatus") int chargeStatus,
			@Param("failedInfo") String failedInfo);

	@Update("UPDATE billing_hotel SET charge_status = 1, charge_time = now(), transaction_num = #{transactionNum}, rgid = #{rgid} WHERE id = #{orderId} and pay_status = 1 and charge_status != 1")
	public int updateChargeOk(@Param("orderId") long orderId, @Param("transactionNum") Long transactionNum,
			@Param("rgid") Long rgid);

	@Update("UPDATE billing_hotel SET charge_failed_info = #{failedInfo}, charge_time = now() WHERE id = #{orderId} and pay_status = 1 and charge_status != 1")
	public int updateFailedInfo(@Param("orderId") long orderId, @Param("failedInfo") String failedInfo);

	@Select("SELECT * FROM billing_hotel WHERE id=#{id} AND pay_status = 1")
	public BillingHotel get(@Param("id") Long id);

}
