/**
 * This class is generated by jOOQ
 */
package de.rwth.dbis.acis.activitytracker.service.dal.jooq.tables;


import de.rwth.dbis.acis.activitytracker.service.dal.jooq.Keys;
import de.rwth.dbis.acis.activitytracker.service.dal.jooq.Reqbaztrack;
import de.rwth.dbis.acis.activitytracker.service.dal.jooq.tables.records.ActivityRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.2"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Activity extends TableImpl<ActivityRecord> {

	private static final long serialVersionUID = 1527543309;

	/**
	 * The reference instance of <code>reqbaztrack.activity</code>
	 */
	public static final Activity ACTIVITY = new Activity();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ActivityRecord> getRecordType() {
		return ActivityRecord.class;
	}

	/**
	 * The column <code>reqbaztrack.activity.Id</code>.
	 */
	public final TableField<ActivityRecord, Integer> ID = createField("Id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>reqbaztrack.activity.creation_time</code>.
	 */
	public final TableField<ActivityRecord, Timestamp> CREATION_TIME = createField("creation_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>reqbaztrack.activity.activity_action</code>.
	 */
	public final TableField<ActivityRecord, String> ACTIVITY_ACTION = createField("activity_action", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

	/**
	 * The column <code>reqbaztrack.activity.data_url</code>.
	 */
	public final TableField<ActivityRecord, String> DATA_URL = createField("data_url", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>reqbaztrack.activity.data_type</code>.
	 */
	public final TableField<ActivityRecord, String> DATA_TYPE = createField("data_type", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

	/**
	 * The column <code>reqbaztrack.activity.data_frontend_url</code>.
	 */
	public final TableField<ActivityRecord, String> DATA_FRONTEND_URL = createField("data_frontend_url", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>reqbaztrack.activity.parent_data_url</code>.
	 */
	public final TableField<ActivityRecord, String> PARENT_DATA_URL = createField("parent_data_url", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>reqbaztrack.activity.parent_data_type</code>.
	 */
	public final TableField<ActivityRecord, String> PARENT_DATA_TYPE = createField("parent_data_type", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>reqbaztrack.activity.user_url</code>.
	 */
	public final TableField<ActivityRecord, String> USER_URL = createField("user_url", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * Create a <code>reqbaztrack.activity</code> table reference
	 */
	public Activity() {
		this("activity", null);
	}

	/**
	 * Create an aliased <code>reqbaztrack.activity</code> table reference
	 */
	public Activity(String alias) {
		this(alias, ACTIVITY);
	}

	private Activity(String alias, Table<ActivityRecord> aliased) {
		this(alias, aliased, null);
	}

	private Activity(String alias, Table<ActivityRecord> aliased, Field<?>[] parameters) {
		super(alias, Reqbaztrack.REQBAZTRACK, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<ActivityRecord, Integer> getIdentity() {
		return Keys.IDENTITY_ACTIVITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ActivityRecord> getPrimaryKey() {
		return Keys.KEY_ACTIVITY_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ActivityRecord>> getKeys() {
		return Arrays.<UniqueKey<ActivityRecord>>asList(Keys.KEY_ACTIVITY_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Activity as(String alias) {
		return new Activity(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Activity rename(String name) {
		return new Activity(name, null);
	}
}
