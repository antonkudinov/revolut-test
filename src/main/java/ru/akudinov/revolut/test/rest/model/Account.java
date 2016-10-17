package ru.akudinov.revolut.test.rest.model;

import lombok.Data;
import lombok.ToString;
import ru.akudinov.revolut.test.rest.adapter.BigDecimalAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;

import static javax.xml.bind.annotation.XmlAccessType.NONE;


@Entity
@Table(name = "ACCOUNT")
@XmlRootElement(name = "Account")
@XmlAccessorType(NONE)
@NamedQueries({
        @NamedQuery(name = "Account.findByNumber", query = "select b from Account b where b.number=:number")
})
@Data
@ToString
public class Account {

    private static final long serialVersionUID = 0;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccountSeq")
    @SequenceGenerator(name = "AccountSeq", sequenceName = "ACCOUNT_SEQ")
    @XmlAttribute
    private Long id;

    @Column(name = "NUMBER", nullable = false, unique = true)
    @XmlAttribute
    private long number;

    @Column(name = "BALANCE", precision = 20, scale = 5, nullable = false)
    @XmlAttribute
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal balance;
}
