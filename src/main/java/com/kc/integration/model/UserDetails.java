package com.kc.integration.model;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "userdetails", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetails {

    @Id
    @Column(name = "userid", length = 12, nullable = false)
    private String userId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sno", nullable = false)
    private BigDecimal sno;

    private String password;

    @Column(name = "firstname_en", length = 100)
    private String firstnameEn;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "lastname_en", length = 100)
    private String lastnameEn;

    @Column(name = "contactnumber", length = 20)
    private String contactNumber;

    @Column(name = "activestatus", length = 1, nullable = false)
    private String activeStatus = "Y";

    @Column(name = "belongs_to", length = 50)
    private String belongsTo;

    @Column(name = "entered_by", length = 12)
    private String enteredBy;

    @Column(name = "entered_date")
    private Timestamp enteredDate;

    @Column(name = "location_id", length = 2)
    private String locationId;

    @Column(name = "manufacturer_id", length = 12)
    private String manufacturerId;

    @Column(name = "usertype_id", length = 12)
    private String userTypeId;

    @Column(length = 50)
    private String email;

    @Column(name = "national_id_no", length = 100)
    private String nationalIdNo;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "mac_id", length = 50)
    private String macId;

    @Column(name = "tin_no", length = 200)
    private String tinNo;

//    @Lob
    @Column(name = "national_id_document")
    private byte[] nationalIdDocument;

    @Column(name = "telephone_code")
    private BigDecimal telephoneCode;

    @Column(name = "mobile_code")
    private BigDecimal mobileCode;

    @Column(name = "login_status", length = 50)
    private String loginStatus;

    @Column(length = 100)
    private String designation;

    @Column(length = 100)
    private String department;

    @Column(length = 300)
    private String organisation;

    @Column(name = "mobile_no", length = 50)
    private String mobileNo;

    @Column(name = "verifycode_pwd", length = 100)
    private String verifyCodePwd;

    private BigDecimal title;

    @Column(name = "isloggedin", length = 1)
    private String isLoggedIn;

    @Column(name = "login_time")
    private Timestamp loginTime;

    @Column(name = "logout_time")
    private Timestamp logoutTime;

    @Column(name = "login_from", length = 100)
    private String loginFrom;

    @Column(name = "factory_id", length = 12)
    private String factoryId;

    @Column(name = "production_line_id", length = 12)
    private String productionLineId;

    @Column(name = "trial747", length = 1)
    private String trial747;

    @Column(length = 20)
    private String city;

    @Column(length = 100)
    private String latitude;

    @Column(length = 100)
    private String longitude;

//    @Lob
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "cr_by", length = 30)
    private String createdBy;

    @Column(name = "cr_dtimes")
    private Timestamp createdDateTime;

    @Column(name = "upd_by", length = 30)
    private String updatedBy;

    @Column(name = "upd_dtimes")
    private Timestamp updatedDateTime;

    @Column
    private String address;

    @Column(name = "address_2")
    private String address2;

    @Column(name = "dateofbirth")
    private String dateOfBirth;

}
