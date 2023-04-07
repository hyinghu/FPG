package EthSign;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "geth_tokens", indexes = {
        @Index(columnList = "contract_id"),
})
public class TokenEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "name")
    private String name;

    @Column(name = "decimals")
    private BigInteger decimals;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date created;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updated;


    public TokenEntity() {
    }

    public TokenEntity(Long contractId, String name, BigInteger decimals) {
        this.contractId = contractId;
        this.name = name;
        this.decimals = decimals;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getDecimals() {
        return decimals;
    }

    public void setDecimals(BigInteger decimals) {
        this.decimals = decimals;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "TokenEntity{" +
                "id=" + id +
                ", contractId=" + contractId +
                ", name='" + name + '\'' +
                ", decimals=" + decimals +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}
