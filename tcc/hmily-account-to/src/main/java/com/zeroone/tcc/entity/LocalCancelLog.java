package com.zeroone.tcc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@ToString
@Table(name = "local_cancel_log")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LocalCancelLog implements Serializable {


    @Id
    @Column(name = "tx_no")
    private String txNumber;


    @CreatedDate
    @Column
    private Date createTime;


    public static LocalCancelLog build(String txNumber){
        LocalCancelLog localCancelLog = new LocalCancelLog();
        localCancelLog.setTxNumber(txNumber);
        return localCancelLog;
    }
}
