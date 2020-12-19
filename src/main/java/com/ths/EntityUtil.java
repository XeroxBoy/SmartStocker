package com.ths;

import java.io.*;
import java.sql.*;
import java.util.*;

public class EntityUtil {

    private final String type_char = "char";

    private final String type_date = "date";

    private final String type_timestamp = "timestamp";

    private final String type_int = "int";

    private final String type_bigint = "bigint";

    private final String type_text = "text";

    private final String type_bit = "bit";

    private final String type_decimal = "decimal";

    private final String type_blob = "blob";

    private final String type_double = "double";

    private final String moduleName = "manhua"; // 数据库名称

    private final String localpath = "/Users/admin/代码/github/MyGithub/crawler-stock-data-all/";

    private final String bean_path = localpath + "crawler-stock-data-all/crawler-ths-data-service/src/main/java";

    private final String mapper_path = localpath + "crawler-stock-data-all/crawler-ths-data-service/src/main/java";

    private final String xml_path = localpath
            + "crawler-stock-data-all/crawler-ths-data-service/src/main/resources/mapper/ths";// xml路径

    private final String model_package = "com.ths.domain";// model的包所在

    private final String dao_package = "com.ths.dao";// dao的包所在

    private final String driverName = "com.mysql.jdbc.Driver";

    private final String user = "root";// 数据库用户

    private final String password = "XIEshuai1122";// 数据库密码

    private final String url = "jdbc:mysql://rm-bp10zmrn4k515v071o.mysql.rds.aliyuncs.com:3306/" + moduleName + "?useUnicode=true&amp;characterEncoding=utf-8";

    private String tableName = null;

    private String beanName = null;

    private String mapperName = null;

    private Connection conn = null;

    private void init() throws ClassNotFoundException, SQLException {
        Class.forName(driverName);
        conn = DriverManager.getConnection(url, user, password);
    }

    /**
     * 获取所有的表
     *
     * @return
     * @throws SQLException
     */
    private List<String> getTables() throws SQLException {
        List<String> tables = new ArrayList<String>();
        PreparedStatement pstate = conn.prepareStatement("show tables");
        ResultSet results = pstate.executeQuery();
        while (results.next()) {
            String tableName = results.getString(1);
            if (tableName.toLowerCase().contains("xiu_xiu_man_hua")) {
                tables.add(tableName);
            }
        }
        return tables;
    }

    private void processTable(String table) {
        StringBuffer sb = new StringBuffer(table.length());
        String tableNew = table.toLowerCase();
        String[] tables = tableNew.split("_");
        String temp = null;
        for (int i = 0; i < tables.length; i++) {
            temp = tables[i].trim();
            sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
        }
        beanName = "" + sb.toString();
        mapperName = beanName;
    }

    private Set<String> processImportType(List<String> columns, List<String> types) {
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < columns.size(); i++) {
            String type = types.get(i);
            if (type.indexOf(type_date) > -1) {
                set.add("import java.util.Date;");
            } else if (type.indexOf(type_timestamp) > -1) {
                set.add("import java.util.Date;");
            } else if (type.indexOf(type_decimal) > -1) {
                set.add("import java.math.BigDecimal;");
            } else if (type.indexOf(type_double) > -1) {
                set.add("import java.math.BigDecimal;");
            }
        }
        return set;
    }

    private String processType(String type) {
        if (type.indexOf(type_char) > -1) {
            return "String";
        } else if (type.indexOf(type_bigint) > -1) {
            return "Long";
        } else if (type.indexOf(type_int) > -1) {
            return "Integer";
        } else if (type.indexOf(type_date) > -1) {
            return "Date";
        } else if (type.indexOf(type_text) > -1) {
            return "String";
        } else if (type.indexOf(type_timestamp) > -1) {
            return "Date";
        } else if (type.indexOf(type_bit) > -1) {
            return "Boolean";
        } else if (type.indexOf(type_decimal) > -1) {
            return "BigDecimal";
        } else if (type.indexOf(type_blob) > -1) {
            return "byte[]";
        } else if (type.indexOf(type_double) > -1) {
            return "BigDecimal";
        }

        return null;
    }

    private boolean processTypeIfCase(String type) {
        if (type.indexOf(type_char) > -1) {
            return true;
        } else if (type.indexOf(type_text) > -1) {
            return true;
        } else {
            return false;
        }
    }
    // private String processType(String type, String enumType) {
    // if (StringUtils.isNotBlank(enumType)) {
    // return enumType;
    // } else if (type.indexOf(type_char) > -1) {
    // return "String";
    // } else if (type.indexOf(type_bigint) > -1) {
    // return "Long";
    // } else if (type.indexOf(type_int) > -1) {
    // return "Integer";
    // } else if (type.indexOf(type_date) > -1) {
    // return "java.util.Date";
    // } else if (type.indexOf(type_text) > -1) {
    // return "String";
    // } else if (type.indexOf(type_timestamp) > -1) {
    // return "java.util.Date";
    // } else if (type.indexOf(type_bit) > -1) {
    // return "Boolean";
    // } else if (type.indexOf(type_decimal) > -1) {
    // return "java.math.BigDecimal";
    // } else if (type.indexOf(type_blob) > -1) {
    // return "byte[]";
    // }
    // return null;
    // }

    private String processField(String field) {
        StringBuffer sb = new StringBuffer(field.length());
        // field = field.toLowerCase();
        String[] fields = field.split("_");
        String temp = null;
        sb.append(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            temp = fields[i].trim();
            sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
        }
        return sb.toString();
    }

    // /**
    // * 将实体类名首字母改为小写
    // *
    // * @param beanName
    // * @return
    // */
    // private String processResultMapId(String beanName) {
    // return beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
    // }

    /**
     * 构建类上面的注释
     *
     * @param bw
     * @param text
     * @return
     * @throws IOException
     */
    private BufferedWriter buildClassComment(BufferedWriter bw, String text) throws IOException {
        bw.newLine();
        bw.newLine();
        bw.write("/**");
        bw.newLine();
        bw.write(" * ");
        bw.newLine();
        bw.write(" * " + text);
        bw.newLine();
        bw.write(" * ");
        bw.newLine();
        bw.write(" **/");
        return bw;
    }

    /**
     * 构建方法上面的注释
     *
     * @param bw
     * @param text
     * @return
     * @throws IOException
     */
    private BufferedWriter buildMethodComment(BufferedWriter bw, String text) throws IOException {
        bw.newLine();
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * ");
        bw.newLine();
        bw.write("\t * " + text);
        bw.newLine();
        bw.write("\t * ");
        bw.newLine();
        bw.write("\t **/");
        return bw;
    }

    /**
     * 生成实体类
     *
     * @param columns
     * @param types
     * @param comments
     * @throws IOException
     */
    private void buildEntityBean(List<String> columns, List<String> types, List<String> comments, String tableComment)
            throws IOException {
        // 创建包目录
        String packagePath = bean_path + File.separator + createPackagePath(model_package);
        File folder = new File(packagePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File beanFile = new File(packagePath, beanName + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(beanFile)));
        bw.write("package " + model_package + ";");
        bw.newLine();
        bw.newLine();
        Set<String> set = processImportType(columns, types);
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String str = it.next();
            bw.write(str);
            bw.newLine();
        }
        // bw.write("import lombok.Data;");
        // bw.write("import javax.persistence.Entity;");
        bw = buildClassComment(bw, tableComment);
        bw.newLine();
        // bw.write("@Entity");
        // bw.write("@Data");
        // bw.newLine();
        bw.write("public class " + beanName + " implements java.io.Serializable {");
        bw.newLine();
        bw.newLine();
        bw.write("\n  private static final long serialVersionUID = 1L;");
        bw.newLine();
        bw.newLine();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            bw.write("\n  /**" + comments.get(i) + "**/");
            bw.newLine();
            bw.write("\n  private " + processType(types.get(i)) + " " + processField(columns.get(i)) + ";");
            bw.newLine();
            bw.newLine();
        }
        bw.newLine();
        // 生成get 和 set方法
        String tempField = null;
        String _tempField = null;
        String tempType = null;
        for (int i = 0; i < size; i++) {
            tempType = processType(types.get(i));
            _tempField = processField(columns.get(i));
            tempField = _tempField.substring(0, 1).toUpperCase() + _tempField.substring(1);
            bw.newLine();
            // bw.write("\tpublic void set" + tempField + "(" + tempType + " _"
            // + _tempField + "){");
            bw.write("\n  public void set" + tempField + "(" + tempType + " " + _tempField + ") { ");
            // bw.write("\t\tthis." + _tempField + "=_" + _tempField + ";");
            bw.write("\r    this." + _tempField + " = " + _tempField + ";");
            bw.write("\n  }");
            bw.newLine();
            bw.newLine();
            bw.write("\n  public " + tempType + " get" + tempField + "() { ");
            bw.write("\r    return this." + _tempField + ";");
            bw.write("\n  }");
            bw.newLine();
        }
        bw.newLine();
        bw.write("}");
        bw.newLine();
        bw.flush();
        bw.close();
    }

    /**
     * 构建Mapper文件
     *
     * @throws IOException
     */
    private void buildMapper(List<String> columns, List<String> types) throws IOException {
        String packagePath = mapper_path + File.separator + createPackagePath(dao_package);
        File folder = new File(packagePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File mapperFile = new File(packagePath, mapperName + "Dao.java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperFile), "utf-8"));
        bw.write("package " + dao_package + ";");
        bw.newLine();
        bw.newLine();
        bw.write("import java.util.List;");
        bw.newLine();
        bw.write("import " + model_package + "." + beanName + ";");
        bw.newLine();
        bw.write("import com.ths.utils.CommonQueryBean;");
        bw.newLine();
        bw.newLine();
        bw.write("import org.apache.ibatis.annotations.Param;");
        bw.newLine();
        bw.write("import org.springframework.stereotype.Repository;");

        bw = buildClassComment(bw, mapperName + "数据库操作接口类");
        bw.newLine();
        bw.newLine();
        // bw.write("public interface " + mapperName + " extends " +
        // mapper_extends + "<" + beanName + "> {");
        bw.write("@Repository");
        bw.newLine();
        bw.write("public interface " + mapperName + "Dao" + "{");
        bw.newLine();
        bw.newLine();
        // ----------定义Mapper中的方法Begin----------
        bw = buildMethodComment(bw, "查询（根据主键ID查询）");
        bw.newLine();
        bw.write("\t" + beanName + "  selectByPrimaryKey ( @Param(\"" + processField(columns.get(0)) + "\") "
                + processType(types.get(0)) + " " + processField(columns.get(0)) + " );");
        bw.newLine();
        bw = buildMethodComment(bw, "删除（根据主键ID删除）");
        bw.newLine();
        bw.write("\t" + "int deleteByPrimaryKey ( @Param(\"" + processField(columns.get(0)) + "\") "
                + processType(types.get(0)) + " " + processField(columns.get(0)) + " );");
        bw.newLine();
        bw = buildMethodComment(bw, "添加");
        bw.newLine();
        bw.write("\t" + "int insert( " + beanName + " record );");
        bw.newLine();
        // bw = buildMethodComment(bw, "添加 （匹配有值的字段）");
        // bw.newLine();
        // bw.write("\t" + "int insertSelective( " + beanName + " record );");
        // bw.newLine();
        bw = buildMethodComment(bw, "修改 （匹配有值的字段）");
        bw.newLine();
        bw.write("\t" + "int updateByPrimaryKeySelective( " + beanName + " record );");
        bw.newLine();
        // bw = buildMethodComment(bw, "修改（根据主键ID修改）");
        // bw.newLine();
        // bw.write("\t" + "int updateByPrimaryKey ( " + beanName + " record
        // );");
        // bw.newLine();

        bw = buildMethodComment(bw, "list分页查询");
        bw.newLine();
        bw.write("\t" + "List<" + beanName + "> list4Page (@Param(\"record\") " + beanName
                + " record, @Param(\"commonQueryParam\") CommonQueryBean query);");
        bw.newLine();

        bw = buildMethodComment(bw, "count查询");
        bw.newLine();
        bw.write("\t" + "long count (@Param(\"record\") " + beanName + " record);");
        bw.newLine();

        bw = buildMethodComment(bw, "list查询");
        bw.newLine();
        bw.write("\t" + "List<" + beanName + "> list (@Param(\"record\") " + beanName + " record);");
        bw.newLine();

        // ----------定义Mapper中的方法End----------
        bw.newLine();
        bw.write("}");
        bw.flush();
        bw.close();
    }

    /**
     * 构建实体类映射XML文件
     *
     * @param columns
     * @param types
     * @param comments
     * @throws IOException
     */
    private void buildMapperXml(List<String> columns, List<String> types, List<String> comments) throws IOException {
        File folder = new File(xml_path, moduleName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File mapperXmlFile = new File(folder, mapperName + ".xml");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperXmlFile)));
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();
        bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" ");
        bw.newLine();
        bw.write("    \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
        bw.newLine();
        bw.write("<mapper namespace=\"" + dao_package + "." + mapperName + "Dao\">");
        bw.newLine();
        bw.newLine();

        /*
         * bw.write("\t<!--实体映射-->"); bw.newLine(); bw.write("\t<resultMap id=\"" +
         * this.processResultMapId(beanName) + "ResultMap\" type=\"" + beanName +
         * "\">"); bw.newLine(); bw.write("\t\t<!--" + comments.get(0) + "-->");
         * bw.newLine(); bw.write("\t\t<id property=\"" +
         * this.processField(columns.get(0)) + "\" column=\"" + columns.get(0) +
         * "\" />"); bw.newLine(); int size = columns.size(); for ( int i = 1 ; i < size
         * ; i++ ) { bw.write("\t\t<!--" + comments.get(i) + "-->"); bw.newLine();
         * bw.write("\t\t<result property=\"" + this.processField(columns.get(i)) +
         * "\" column=\"" + columns.get(i) + "\" />"); bw.newLine(); }
         * bw.write("\t</resultMap>"); bw.newLine(); bw.newLine(); bw.newLine();
         */

        // 下面开始写SqlMapper中的方法
        // this.outputSqlMapperMethod(bw, columns, types);
        buildSQL(bw, columns, types);

        bw.write("</mapper>");
        bw.flush();
        bw.close();
    }

    private void buildSQL(BufferedWriter bw, List<String> columns, List<String> types) throws IOException {

        bw.write("\t<resultMap id=\"" + beanName + "\" type=\"" + model_package + "." + beanName + "\" >");
        bw.newLine();
        // <result column="$!item.columnName"
        // property="$!item.formatColumnName"/>
        for (int i = 0; i < columns.size(); i++) {
            bw.write("\t\t" + "<result column=\"" + columns.get(i) + "\" property=\"" + processField(columns.get(i))
                    + "\"/>");
            bw.newLine();
        }
        bw.write("\t</resultMap>");
        bw.newLine();
        bw.newLine();

        int size = columns.size();
        // 通用结果列
        bw.write("\t<!-- 通用查询结果列-->");
        bw.newLine();
        bw.write("\t<sql id=\"Base_Column_List\">");
        bw.newLine();

        for (int i = 0; i < size; i++) {
            bw.write("\t\t" + columns.get(i));
            if (i != size - 1) {
                bw.write(",");
                bw.newLine();
            }
        }

        bw.newLine();
        bw.write("\t</sql>");
        bw.newLine();
        bw.newLine();

        // 查询（根据主键ID查询）
        bw.write("\t<!-- 查询（根据主键ID查询） -->");
        bw.newLine();
        bw.write("\t<select id=\"selectByPrimaryKey\" resultMap=\"" + beanName + "\" parameterType=\"java.lang."
                + processType(types.get(0)) + "\">");
        bw.newLine();
        bw.write("\t\t SELECT");
        bw.newLine();
        bw.write("\t\t <include refid=\"Base_Column_List\" />");
        bw.newLine();
        bw.write("\t\t FROM " + tableName);
        bw.newLine();
        bw.write("\t\t WHERE " + columns.get(0) + " = #{" + processField(columns.get(0)) + "}");
        bw.newLine();
        bw.write("\t</select>");
        bw.newLine();
        bw.newLine();
        // 查询完

        // 删除（根据主键ID删除）
        bw.write("\t<!--删除：根据主键ID删除-->");
        bw.newLine();
        bw.write("\t<delete id=\"deleteByPrimaryKey\" parameterType=\"java.lang." + processType(types.get(0)) + "\">");
        bw.newLine();
        bw.write("\t\t DELETE FROM " + tableName);
        bw.newLine();
        bw.write("\t\t WHERE " + columns.get(0) + " = #{" + processField(columns.get(0)) + "}");
        bw.newLine();
        bw.write("\t</delete>");
        bw.newLine();
        bw.newLine();
        // 删除完

        // 添加insert方法
        bw.write("\t<!-- 添加 -->");
        bw.newLine();
        bw.write("\t<insert id=\"insert\" parameterType=\"" + model_package + "." + beanName + "\">");
        bw.newLine();
        bw.write("\t\t INSERT INTO " + tableName);
        bw.newLine();
        bw.write(" \t\t(");
        bw.newLine();
        for (int i = 0; i < size; i++) {
            bw.write("\t\t\t " + columns.get(i));
            if (i != size - 1) {
                bw.write(",");
            }
            bw.newLine();
        }
        bw.write("\t\t) ");
        bw.newLine();
        bw.write("\t\t VALUES ");
        bw.newLine();
        bw.write(" \t\t(");
        bw.newLine();
        for (int i = 0; i < size; i++) {
            bw.write("\t\t\t " + "#{" + processField(columns.get(i)) + "}");
            if (i != size - 1) {
                bw.write(",");
            }
            bw.newLine();
        }
        bw.write(" \t\t) ");
        bw.newLine();
        bw.write("\t\t <selectKey keyProperty=\"" + processField(columns.get(0)) + "\" resultType=\""
                + processType(types.get(0)) + "\" order=\"AFTER\">");
        bw.newLine();
        bw.write("\t\t\t select LAST_INSERT_ID()");
        bw.newLine();
        bw.write("\t\t </selectKey>");
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();
        bw.newLine();

        // 添加insert完

        // --------------- insert方法（匹配有值的字段）
        // bw.write("\t<!-- 添加 （匹配有值的字段）-->");
        // bw.newLine();
        // bw.write("\t<insert id=\"insertSelective\" parameterType=\"" +
        // processResultMapId(beanName) + "\">");
        // bw.newLine();
        // bw.write("\t\t INSERT INTO " + tableName);
        // bw.newLine();
        // bw.write("\t\t <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\"
        // >");
        // bw.newLine();
        //
        // String tempField = null;
        // for (int i = 0; i < size; i++) {
        // tempField = processField(columns.get(i));
        // bw.write("\t\t\t<if test=\"" + tempField + " != null\">");
        // bw.newLine();
        // bw.write("\t\t\t\t " + columns.get(i) + ",");
        // bw.newLine();
        // bw.write("\t\t\t</if>");
        // bw.newLine();
        // }
        //
        // bw.newLine();
        // bw.write("\t\t </trim>");
        // bw.newLine();
        //
        // bw.write("\t\t <trim prefix=\"values (\" suffix=\")\"
        // suffixOverrides=\",\" >");
        // bw.newLine();
        //
        // tempField = null;
        // for (int i = 0; i < size; i++) {
        // tempField = processField(columns.get(i));
        // bw.write("\t\t\t<if test=\"" + tempField + "!=null\">");
        // bw.newLine();
        // bw.write("\t\t\t\t #{" + tempField + "},");
        // bw.newLine();
        // bw.write("\t\t\t</if>");
        // bw.newLine();
        // }
        //
        // bw.write("\t\t </trim>");
        // bw.newLine();
        // bw.write("\t</insert>");
        // bw.newLine();
        // bw.newLine();
        // // --------------- 完毕

        // 修改update方法
        String tempField = null;
        bw.write("\t<!-- 修 改-->");
        bw.newLine();
        bw.write("\t<update id=\"updateByPrimaryKeySelective\" parameterType=\"" + model_package + "." + beanName
                + "\">");
        bw.newLine();
        bw.write("\t\t UPDATE " + tableName);
        bw.newLine();
        bw.write(" \t\t <set> ");
        bw.newLine();

        tempField = null;
        for (int i = 1; i < size; i++) {
            tempField = processField(columns.get(i));
            if (processTypeIfCase(types.get(i))) {
                bw.write("\t\t\t<if test=\"" + tempField + " != null and " + tempField + " != ''\">");
            } else {
                bw.write("\t\t\t<if test=\"" + tempField + " != null\">");
            }
            bw.newLine();
            bw.write("\t\t\t\t " + columns.get(i) + " = #{" + tempField + "},");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }

        bw.newLine();
        bw.write(" \t\t </set>");
        bw.newLine();
        bw.write("\t\t WHERE " + columns.get(0) + " = #{" + processField(columns.get(0)) + "}");
        bw.newLine();
        bw.write("\t</update>");
        bw.newLine();
        bw.newLine();
        // update方法完毕

        // ----- 修改（匹配有值的字段）
        // bw.write("\t<!-- 修 改-->");
        // bw.newLine();
        // bw.write("\t<update id=\"updateByPrimaryKey\" parameterType=\"" +
        // processResultMapId(beanName) + "\">");
        // bw.newLine();
        // bw.write("\t\t UPDATE " + tableName);
        // bw.newLine();
        // bw.write("\t\t SET ");
        //
        // bw.newLine();
        // tempField = null;
        // for (int i = 1; i < size; i++) {
        // tempField = processField(columns.get(i));
        // bw.write("\t\t\t " + columns.get(i) + " = #{" + tempField + "}");
        // if (i != size - 1) {
        // bw.write(",");
        // }
        // bw.newLine();
        // }
        //
        // bw.write("\t\t WHERE " + columns.get(0) + " = #{" +
        // processField(columns.get(0)) + "}");
        // bw.newLine();
        // bw.write("\t</update>");
        // bw.newLine();
        // bw.newLine();

        // list4Page方法
        tempField = null;
        bw.write("\t<!-- list4Page 分页查询-->");
        bw.newLine();
        bw.write("\t<select id=\"list4Page\" resultMap=\"" + beanName + "\">");
        bw.newLine();
        bw.write("\t\t SELECT ");
        bw.newLine();
        bw.write("\t\t <include refid=\"Base_Column_List\" />");
        bw.newLine();
        bw.write("\t\t from " + tableName);
        bw.newLine();
        bw.write(" \t\t where 1=1  ");
        bw.newLine();
        tempField = null;
        for (int i = 0; i < size; i++) {
            tempField = processField(columns.get(i));
            if (processTypeIfCase(types.get(i))) {
                bw.write("\t\t<if test=\"record." + tempField + " != null and record." + tempField + " != ''\">");
            } else {
                bw.write("\t\t<if test=\"record." + tempField + " != null\">");
            }
            bw.newLine();
            bw.write("\t\t\t and " + columns.get(i) + " = #{record." + tempField + "} ");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t<if test=\"" + "commonQueryParam" + " != null\">");
        bw.newLine();
        bw.write("\t\t\t<if test=\"commonQueryParam.start != null  and commonQueryParam.pageSize != null\">");
        bw.newLine();
        bw.write("\t\t\t\t limit #{commonQueryParam.start}, #{commonQueryParam.pageSize}");
        bw.newLine();
        bw.write("\t\t\t</if>");
        bw.newLine();
        bw.write("\t\t</if>");
        bw.newLine();
        bw.write("\t</select>");
        bw.newLine();

        // count方法
        tempField = null;
        bw.write("\t<!-- count 总数-->");
        bw.newLine();
        bw.write("\t<select id=\"count\" resultType=\"long\">");
        bw.newLine();
        bw.write("\t\t SELECT ");
        bw.newLine();
        bw.write("\t\t count(1) ");
        bw.newLine();
        bw.write("\t\t from " + tableName);
        bw.newLine();
        bw.write(" \t\t where 1=1  ");
        bw.newLine();
        tempField = null;
        for (int i = 0; i < size; i++) {
            tempField = processField(columns.get(i));
            if (processTypeIfCase(types.get(i))) {
                bw.write("\t\t<if test=\"record." + tempField + " != null and record." + tempField + " != ''\">");
            } else {
                bw.write("\t\t<if test=\"record." + tempField + " != null\">");
            }
            bw.newLine();
            bw.write("\t\t\t and " + columns.get(i) + " = #{record." + tempField + "} ");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
        }
        bw.write("\t</select>");
        bw.newLine();

        // list方法
        tempField = null;
        bw.write("\t<!-- list 查询-->");
        bw.newLine();
        bw.write("\t<select id=\"list\" resultMap=\"" + beanName + "\">");
        bw.newLine();
        bw.write("\t\t SELECT ");
        bw.newLine();
        bw.write("\t\t <include refid=\"Base_Column_List\" />");
        bw.newLine();
        bw.write("\t\t from " + tableName);
        bw.newLine();
        bw.write(" \t\t where 1=1  ");
        bw.newLine();
        tempField = null;
        for (int i = 0; i < size; i++) {
            tempField = processField(columns.get(i));
            if (processTypeIfCase(types.get(i))) {
                bw.write("\t\t<if test=\"record." + tempField + " != null and record." + tempField + " != ''\">");
            } else {
                bw.write("\t\t<if test=\"record." + tempField + " != null\">");
            }
            bw.newLine();
            bw.write("\t\t\t and " + columns.get(i) + " = #{record." + tempField + "} ");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
        }
        bw.write("\t</select>");
        bw.newLine();
    }

    /**
     * 获取所有的数据库表注释
     *
     * @return
     * @throws SQLException
     */
    private Map<String, String> getTableComment() throws SQLException {
        Map<String, String> maps = new HashMap<String, String>();
        PreparedStatement pstate = conn.prepareStatement("show table status");
        ResultSet results = pstate.executeQuery();
        while (results.next()) {
            String tableName = results.getString("NAME");
            String comment = results.getString("COMMENT");
            maps.put(tableName, comment);
        }
        return maps;
    }

    public void generate() throws ClassNotFoundException, SQLException, IOException {
        init();
        String prefix = "show full fields from ";
        List<String> columns = null;
        List<String> types = null;
        List<String> comments = null;
        PreparedStatement pstate = null;
        List<String> tables = getTables();
        Map<String, String> tableComments = getTableComment();
        for (String table : tables) {
            // String table="risk_assess_request";
            columns = new ArrayList<String>();
            types = new ArrayList<String>();
            comments = new ArrayList<String>();
            pstate = conn.prepareStatement(prefix + table);
            ResultSet results = pstate.executeQuery();
            while (results.next()) {
                columns.add(results.getString("FIELD").toLowerCase());
                types.add(results.getString("TYPE"));
                comments.add(results.getString("COMMENT"));
            }
            tableName = table;
            processTable(table);
            // this.outputBaseBean();
            String tableComment = tableComments.get(tableName);
            buildEntityBean(columns, types, comments, tableComment);
            buildMapper(columns, types);
            buildMapperXml(columns, types, comments);
        }
        conn.close();
    }

    public static String createPackagePath(String packageName) {
        StringBuffer sbBuffer = new StringBuffer();
        String[] arrs = packageName.split("\\.");
        for (String str : arrs) {
            sbBuffer.append(str).append(File.separator);
        }
        return sbBuffer.toString();
    }

    public static void main(String[] args) {
        try {
            new EntityUtil().generate();
            // 自动打开生成文件的目录
            // Runtime.getRuntime().exec("cmd /c start explorer
            // G:\\mybatiestest\\");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}