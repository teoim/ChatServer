package org.mtr.web.api.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mtr.logger.ErrorLogger;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;

@Data
@AllArgsConstructor
public class TextMessageDTO implements Comparable<TextMessageDTO>{

    private Timestamp timestamp;
    private String from;
    private String to;
    private String content;

    @Override
    public int compareTo(TextMessageDTO o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }

    @Override
    public String toString(){
        return "{" + this.getTimestamp() + ": " +
                this.getFrom() + " " +
                this.getTo() + " " +
                this.getContent() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TextMessageDTO that = (TextMessageDTO) o;
        return Objects.equals(timestamp, that.timestamp) && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, from, to, content);
    }

}
