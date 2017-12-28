/*
 * This file is generated by jOOQ.
*/
package de.rwth.dbis.acis.activitytracker.service.dal.jooq.tables.records;


import de.rwth.dbis.acis.activitytracker.service.dal.jooq.tables.Activity;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ActivityRecord extends UpdatableRecordImpl<ActivityRecord> implements Record12<Integer, Timestamp, String, String, String, String, String, String, String, String, Object, Byte> {

    private static final long serialVersionUID = 1946192784;

    /**
     * Setter for <code>reqbaztrack.activity.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>reqbaztrack.activity.creation_date</code>.
     */
    public void setCreationDate(Timestamp value) {
        set(1, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.creation_date</code>.
     */
    public Timestamp getCreationDate() {
        return (Timestamp) get(1);
    }

    /**
     * Setter for <code>reqbaztrack.activity.activity_action</code>.
     */
    public void setActivityAction(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.activity_action</code>.
     */
    public String getActivityAction() {
        return (String) get(2);
    }

    /**
     * Setter for <code>reqbaztrack.activity.origin</code>.
     */
    public void setOrigin(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.origin</code>.
     */
    public String getOrigin() {
        return (String) get(3);
    }

    /**
     * Setter for <code>reqbaztrack.activity.data_url</code>.
     */
    public void setDataUrl(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.data_url</code>.
     */
    public String getDataUrl() {
        return (String) get(4);
    }

    /**
     * Setter for <code>reqbaztrack.activity.data_type</code>.
     */
    public void setDataType(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.data_type</code>.
     */
    public String getDataType() {
        return (String) get(5);
    }

    /**
     * Setter for <code>reqbaztrack.activity.data_frontend_url</code>.
     */
    public void setDataFrontendUrl(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.data_frontend_url</code>.
     */
    public String getDataFrontendUrl() {
        return (String) get(6);
    }

    /**
     * Setter for <code>reqbaztrack.activity.parent_data_url</code>.
     */
    public void setParentDataUrl(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.parent_data_url</code>.
     */
    public String getParentDataUrl() {
        return (String) get(7);
    }

    /**
     * Setter for <code>reqbaztrack.activity.parent_data_type</code>.
     */
    public void setParentDataType(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.parent_data_type</code>.
     */
    public String getParentDataType() {
        return (String) get(8);
    }

    /**
     * Setter for <code>reqbaztrack.activity.user_url</code>.
     */
    public void setUserUrl(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.user_url</code>.
     */
    public String getUserUrl() {
        return (String) get(9);
    }

    /**
     * Setter for <code>reqbaztrack.activity.additional_object</code>.
     */
    public void setAdditionalObject(Object value) {
        set(10, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.additional_object</code>.
     */
    public Object getAdditionalObject() {
        return (Object) get(10);
    }

    /**
     * Setter for <code>reqbaztrack.activity.public</code>.
     */
    public void setPublic(Byte value) {
        set(11, value);
    }

    /**
     * Getter for <code>reqbaztrack.activity.public</code>.
     */
    public Byte getPublic() {
        return (Byte) get(11);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Integer, Timestamp, String, String, String, String, String, String, String, String, Object, Byte> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Integer, Timestamp, String, String, String, String, String, String, String, String, Object, Byte> valuesRow() {
        return (Row12) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Activity.ACTIVITY.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field2() {
        return Activity.ACTIVITY.CREATION_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Activity.ACTIVITY.ACTIVITY_ACTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Activity.ACTIVITY.ORIGIN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Activity.ACTIVITY.DATA_URL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Activity.ACTIVITY.DATA_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Activity.ACTIVITY.DATA_FRONTEND_URL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Activity.ACTIVITY.PARENT_DATA_URL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Activity.ACTIVITY.PARENT_DATA_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Activity.ACTIVITY.USER_URL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Object> field11() {
        return Activity.ACTIVITY.ADDITIONAL_OBJECT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field12() {
        return Activity.ACTIVITY.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value2() {
        return getCreationDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getActivityAction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getOrigin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getDataUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getDataType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getDataFrontendUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getParentDataUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getParentDataType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getUserUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object value11() {
        return getAdditionalObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value12() {
        return getPublic();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value2(Timestamp value) {
        setCreationDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value3(String value) {
        setActivityAction(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value4(String value) {
        setOrigin(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value5(String value) {
        setDataUrl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value6(String value) {
        setDataType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value7(String value) {
        setDataFrontendUrl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value8(String value) {
        setParentDataUrl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value9(String value) {
        setParentDataType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value10(String value) {
        setUserUrl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value11(Object value) {
        setAdditionalObject(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord value12(Byte value) {
        setPublic(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityRecord values(Integer value1, Timestamp value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9, String value10, Object value11, Byte value12) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ActivityRecord
     */
    public ActivityRecord() {
        super(Activity.ACTIVITY);
    }

    /**
     * Create a detached, initialised ActivityRecord
     */
    public ActivityRecord(Integer id, Timestamp creationDate, String activityAction, String origin, String dataUrl, String dataType, String dataFrontendUrl, String parentDataUrl, String parentDataType, String userUrl, Object additionalObject, Byte public_) {
        super(Activity.ACTIVITY);

        set(0, id);
        set(1, creationDate);
        set(2, activityAction);
        set(3, origin);
        set(4, dataUrl);
        set(5, dataType);
        set(6, dataFrontendUrl);
        set(7, parentDataUrl);
        set(8, parentDataType);
        set(9, userUrl);
        set(10, additionalObject);
        set(11, public_);
    }
}
