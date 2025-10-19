package com.mexazon.app.model;

import java.time.LocalTime;
import jakarta.persistence.*;

@Entity
@Table(name="business_hours")
public class BusinessHours {

    @EmbeddedId
    private BusinessHoursId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("businessId") 
    @JoinColumn(name = "business_id")
    private Business business;

    @Column(name = "time_in")
    private LocalTime timeIn;

    @Column(name = "time_out")
    private LocalTime timeOut;

    @Column(name = "is_working", nullable = false)
    private boolean isWorking;

    
    
	public LocalTime getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(LocalTime timeIn) {
		this.timeIn = timeIn;
	}

	public LocalTime getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(LocalTime timeOut) {
		this.timeOut = timeOut;
	}

	public boolean isWorking() {
		return isWorking;
	}

	public void setWorking(boolean isWorking) {
		this.isWorking = isWorking;
	}

	public BusinessHoursId getId() {
		return id;
	}

	public Business getBusiness() {
		return business;
	}

	 public void setId(BusinessHoursId id) {
        this.id = id;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

}
