package ru.akudinov.revolut.test.rest.model;

import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

@Entity
@Table(name = "PAYMENT")
@XmlRootElement(name = "Payment")
@XmlAccessorType(NONE)
@Data
public class Payment {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PaymentSeq")
    @SequenceGenerator(name = "PaymentSeq", sequenceName = "PAYMENT_SEQ")
    @XmlAttribute
    private Long id;

    @Column(name = "WITHDRAWAL_ACCOUNT_NUMBER", nullable = false)
    @XmlAttribute
    private long withdrawalAccountNumber;

    @Column(name = "DEPOSIT_ACCOUNT_NUMBER", nullable = false)
    @XmlAttribute
    private long depositAccountNumber;

    @Column(name = "AMOUNT", precision = 20, scale = 5, nullable = false)
    @XmlAttribute
    private BigDecimal amount;

    @Column(name = "DATE", nullable = false)
    @Temporal(TIMESTAMP)
    @XmlAttribute
    private Date date;
}
