package com.tictactoe.backend.Entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class SpringSessionAttributesEntityPK implements Serializable {
    private String sessionPrimaryId;
    private String attributeName;

    @Column(name = "session_primary_id")
    @Id
    public String getSessionPrimaryId() {
        return sessionPrimaryId;
    }

    public void setSessionPrimaryId(String sessionPrimaryId) {
        this.sessionPrimaryId = sessionPrimaryId;
    }

    @Column(name = "attribute_name")
    @Id
    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpringSessionAttributesEntityPK that = (SpringSessionAttributesEntityPK) o;

        if (sessionPrimaryId != null ? !sessionPrimaryId.equals(that.sessionPrimaryId) : that.sessionPrimaryId != null)
            return false;
        if (attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sessionPrimaryId != null ? sessionPrimaryId.hashCode() : 0;
        result = 31 * result + (attributeName != null ? attributeName.hashCode() : 0);
        return result;
    }
}
