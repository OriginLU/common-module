package com.zeroone.rocketmq.transaction.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "local_try_log")
@Data
@ToString
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LocalTryLog implements Serializable {


    @Id
    @Column(name = "tx_no")
    private String txNumber;


    @CreatedDate
    @Column
    private Date createTime;


    public static LocalTryLog build(String txNumber){
        LocalTryLog localCancelLog = new LocalTryLog();
        localCancelLog.setTxNumber(txNumber);
        return localCancelLog;
    }
}
