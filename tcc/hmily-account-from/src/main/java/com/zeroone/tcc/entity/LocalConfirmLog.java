package com.zeroone.tcc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "local_confirm_log")
@Data
@ToString
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LocalConfirmLog implements Serializable {

    @Id
    @Column(name = "tx_no")
    private String txNumber;

    @CreatedDate
    @Column
    private Date createTime;

    public static LocalConfirmLog build(String txNumber){
        LocalConfirmLog localCancelLog = new LocalConfirmLog();
        localCancelLog.setTxNumber(txNumber);
        return localCancelLog;
    }
}
