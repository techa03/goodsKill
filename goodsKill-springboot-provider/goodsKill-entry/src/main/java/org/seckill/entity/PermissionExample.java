package org.seckill.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PermissionExample implements Serializable{
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PermissionExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andPermissionIdIsNull() {
            addCriterion("permission_id is null");
            return (Criteria) this;
        }

        public Criteria andPermissionIdIsNotNull() {
            addCriterion("permission_id is not null");
            return (Criteria) this;
        }

        public Criteria andPermissionIdEqualTo(Integer value) {
            addCriterion("permission_id =", value, "permissionId");
            return (Criteria) this;
        }

        public Criteria andPermissionIdNotEqualTo(Integer value) {
            addCriterion("permission_id <>", value, "permissionId");
            return (Criteria) this;
        }

        public Criteria andPermissionIdGreaterThan(Integer value) {
            addCriterion("permission_id >", value, "permissionId");
            return (Criteria) this;
        }

        public Criteria andPermissionIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("permission_id >=", value, "permissionId");
            return (Criteria) this;
        }

        public Criteria andPermissionIdLessThan(Integer value) {
            addCriterion("permission_id <", value, "permissionId");
            return (Criteria) this;
        }

        public Criteria andPermissionIdLessThanOrEqualTo(Integer value) {
            addCriterion("permission_id <=", value, "permissionId");
            return (Criteria) this;
        }

        public Criteria andPermissionIdIn(List<Integer> values) {
            addCriterion("permission_id in", values, "permissionId");
            return (Criteria) this;
        }

        public Criteria andPermissionIdNotIn(List<Integer> values) {
            addCriterion("permission_id not in", values, "permissionId");
            return (Criteria) this;
        }

        public Criteria andPermissionIdBetween(Integer value1, Integer value2) {
            addCriterion("permission_id between", value1, value2, "permissionId");
            return (Criteria) this;
        }

        public Criteria andPermissionIdNotBetween(Integer value1, Integer value2) {
            addCriterion("permission_id not between", value1, value2, "permissionId");
            return (Criteria) this;
        }

        public Criteria andPermissionNameIsNull() {
            addCriterion("permission_name is null");
            return (Criteria) this;
        }

        public Criteria andPermissionNameIsNotNull() {
            addCriterion("permission_name is not null");
            return (Criteria) this;
        }

        public Criteria andPermissionNameEqualTo(String value) {
            addCriterion("permission_name =", value, "permissionName");
            return (Criteria) this;
        }

        public Criteria andPermissionNameNotEqualTo(String value) {
            addCriterion("permission_name <>", value, "permissionName");
            return (Criteria) this;
        }

        public Criteria andPermissionNameGreaterThan(String value) {
            addCriterion("permission_name >", value, "permissionName");
            return (Criteria) this;
        }

        public Criteria andPermissionNameGreaterThanOrEqualTo(String value) {
            addCriterion("permission_name >=", value, "permissionName");
            return (Criteria) this;
        }

        public Criteria andPermissionNameLessThan(String value) {
            addCriterion("permission_name <", value, "permissionName");
            return (Criteria) this;
        }

        public Criteria andPermissionNameLessThanOrEqualTo(String value) {
            addCriterion("permission_name <=", value, "permissionName");
            return (Criteria) this;
        }

        public Criteria andPermissionNameLike(String value) {
            addCriterion("permission_name like", value, "permissionName");
            return (Criteria) this;
        }

        public Criteria andPermissionNameNotLike(String value) {
            addCriterion("permission_name not like", value, "permissionName");
            return (Criteria) this;
        }

        public Criteria andPermissionNameIn(List<String> values) {
            addCriterion("permission_name in", values, "permissionName");
            return (Criteria) this;
        }

        public Criteria andPermissionNameNotIn(List<String> values) {
            addCriterion("permission_name not in", values, "permissionName");
            return (Criteria) this;
        }

        public Criteria andPermissionNameBetween(String value1, String value2) {
            addCriterion("permission_name between", value1, value2, "permissionName");
            return (Criteria) this;
        }

        public Criteria andPermissionNameNotBetween(String value1, String value2) {
            addCriterion("permission_name not between", value1, value2, "permissionName");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuIsNull() {
            addCriterion("permission_menu is null");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuIsNotNull() {
            addCriterion("permission_menu is not null");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuEqualTo(String value) {
            addCriterion("permission_menu =", value, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuNotEqualTo(String value) {
            addCriterion("permission_menu <>", value, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuGreaterThan(String value) {
            addCriterion("permission_menu >", value, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuGreaterThanOrEqualTo(String value) {
            addCriterion("permission_menu >=", value, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuLessThan(String value) {
            addCriterion("permission_menu <", value, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuLessThanOrEqualTo(String value) {
            addCriterion("permission_menu <=", value, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuLike(String value) {
            addCriterion("permission_menu like", value, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuNotLike(String value) {
            addCriterion("permission_menu not like", value, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuIn(List<String> values) {
            addCriterion("permission_menu in", values, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuNotIn(List<String> values) {
            addCriterion("permission_menu not in", values, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuBetween(String value1, String value2) {
            addCriterion("permission_menu between", value1, value2, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andPermissionMenuNotBetween(String value1, String value2) {
            addCriterion("permission_menu not between", value1, value2, "permissionMenu");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdIsNull() {
            addCriterion("parent_permission_id is null");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdIsNotNull() {
            addCriterion("parent_permission_id is not null");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdEqualTo(Integer value) {
            addCriterion("parent_permission_id =", value, "parentPermissionId");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdNotEqualTo(Integer value) {
            addCriterion("parent_permission_id <>", value, "parentPermissionId");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdGreaterThan(Integer value) {
            addCriterion("parent_permission_id >", value, "parentPermissionId");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("parent_permission_id >=", value, "parentPermissionId");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdLessThan(Integer value) {
            addCriterion("parent_permission_id <", value, "parentPermissionId");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdLessThanOrEqualTo(Integer value) {
            addCriterion("parent_permission_id <=", value, "parentPermissionId");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdIn(List<Integer> values) {
            addCriterion("parent_permission_id in", values, "parentPermissionId");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdNotIn(List<Integer> values) {
            addCriterion("parent_permission_id not in", values, "parentPermissionId");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdBetween(Integer value1, Integer value2) {
            addCriterion("parent_permission_id between", value1, value2, "parentPermissionId");
            return (Criteria) this;
        }

        public Criteria andParentPermissionIdNotBetween(Integer value1, Integer value2) {
            addCriterion("parent_permission_id not between", value1, value2, "parentPermissionId");
            return (Criteria) this;
        }

        public Criteria andIsDirIsNull() {
            addCriterion("is_dir is null");
            return (Criteria) this;
        }

        public Criteria andIsDirIsNotNull() {
            addCriterion("is_dir is not null");
            return (Criteria) this;
        }

        public Criteria andIsDirEqualTo(String value) {
            addCriterion("is_dir =", value, "isDir");
            return (Criteria) this;
        }

        public Criteria andIsDirNotEqualTo(String value) {
            addCriterion("is_dir <>", value, "isDir");
            return (Criteria) this;
        }

        public Criteria andIsDirGreaterThan(String value) {
            addCriterion("is_dir >", value, "isDir");
            return (Criteria) this;
        }

        public Criteria andIsDirGreaterThanOrEqualTo(String value) {
            addCriterion("is_dir >=", value, "isDir");
            return (Criteria) this;
        }

        public Criteria andIsDirLessThan(String value) {
            addCriterion("is_dir <", value, "isDir");
            return (Criteria) this;
        }

        public Criteria andIsDirLessThanOrEqualTo(String value) {
            addCriterion("is_dir <=", value, "isDir");
            return (Criteria) this;
        }

        public Criteria andIsDirLike(String value) {
            addCriterion("is_dir like", value, "isDir");
            return (Criteria) this;
        }

        public Criteria andIsDirNotLike(String value) {
            addCriterion("is_dir not like", value, "isDir");
            return (Criteria) this;
        }

        public Criteria andIsDirIn(List<String> values) {
            addCriterion("is_dir in", values, "isDir");
            return (Criteria) this;
        }

        public Criteria andIsDirNotIn(List<String> values) {
            addCriterion("is_dir not in", values, "isDir");
            return (Criteria) this;
        }

        public Criteria andIsDirBetween(String value1, String value2) {
            addCriterion("is_dir between", value1, value2, "isDir");
            return (Criteria) this;
        }

        public Criteria andIsDirNotBetween(String value1, String value2) {
            addCriterion("is_dir not between", value1, value2, "isDir");
            return (Criteria) this;
        }

        public Criteria andOrderNoIsNull() {
            addCriterion("order_no is null");
            return (Criteria) this;
        }

        public Criteria andOrderNoIsNotNull() {
            addCriterion("order_no is not null");
            return (Criteria) this;
        }

        public Criteria andOrderNoEqualTo(Integer value) {
            addCriterion("order_no =", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoNotEqualTo(Integer value) {
            addCriterion("order_no <>", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoGreaterThan(Integer value) {
            addCriterion("order_no >", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoGreaterThanOrEqualTo(Integer value) {
            addCriterion("order_no >=", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoLessThan(Integer value) {
            addCriterion("order_no <", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoLessThanOrEqualTo(Integer value) {
            addCriterion("order_no <=", value, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoIn(List<Integer> values) {
            addCriterion("order_no in", values, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoNotIn(List<Integer> values) {
            addCriterion("order_no not in", values, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoBetween(Integer value1, Integer value2) {
            addCriterion("order_no between", value1, value2, "orderNo");
            return (Criteria) this;
        }

        public Criteria andOrderNoNotBetween(Integer value1, Integer value2) {
            addCriterion("order_no not between", value1, value2, "orderNo");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria implements Serializable {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion implements Serializable {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}