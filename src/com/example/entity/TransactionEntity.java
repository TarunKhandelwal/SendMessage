package com.example.entity;

import java.util.Date;

public class TransactionEntity {

	private Integer ID;
	private Date T_DATE;
	private Double T_AMOUNT;
	private Integer PARTY_ID;
	private Integer SALES_PERSON_ID;

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public Date getT_DATE() {
		return T_DATE;
	}

	public void setT_DATE(Date t_DATE) {
		T_DATE = t_DATE;
	}

	public Double getT_AMOUNT() {
		return T_AMOUNT;
	}

	public void setT_AMOUNT(Double t_AMOUNT) {
		T_AMOUNT = t_AMOUNT;
	}

	public Integer getPARTY_ID() {
		return PARTY_ID;
	}

	public void setPARTY_ID(Integer pARTY_ID) {
		PARTY_ID = pARTY_ID;
	}

	public Integer getSALES_PERSON_ID() {
		return SALES_PERSON_ID;
	}

	public void setSALES_PERSON_ID(Integer sALES_PERSON_ID) {
		SALES_PERSON_ID = sALES_PERSON_ID;
	}

}
