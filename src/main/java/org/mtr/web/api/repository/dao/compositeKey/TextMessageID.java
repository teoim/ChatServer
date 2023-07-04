package org.mtr.web.api.repository.dao.compositeKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TextMessageID implements Serializable{

        private Timestamp timestamp;
        private String txtFrom;

}
