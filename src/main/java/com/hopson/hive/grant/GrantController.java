package com.hopson.hive.grant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.AbstractSemanticAnalyzerHook;
import org.apache.hadoop.hive.ql.parse.HiveSemanticAnalyzerHookContext;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.session.SessionState;


public class GrantController extends AbstractSemanticAnalyzerHook implements Configurable {

    private Configuration conf;
    private Log LOG = LogFactory.getLog(GrantController.class);

    //配置的admin权限的账号
    private static String[] admins = {"hive", "hadoop", "root", "hdfs", "easylife", "hst"};

    @Override
    public ASTNode preAnalyze(HiveSemanticAnalyzerHookContext context, ASTNode ast) throws SemanticException {
        String keys = "";
        if (!keys.equalsIgnoreCase("")) {
            admins = keys.split(",");
        }
        int type = -999;
        if (ast != null && ast.getToken() != null) {
            type = ast.getToken().getType();
        }
        LOG.info("token type is :" + type);
        switch (ast.getToken().getType()) {
            case 698: //GRANT select on database easylife_ods to user easylife_analysis;
            case 799: //REVOKE SELECT on database easylife_ods from user easylife_analysis;
            case 825: //show roles
            case 652: //create role test;
            case 681: //drop role test;
//            case 824: //SHOW GRANT;
            case 826: //SHOW ROLE GRANT user easylife;
                String userName = null;
                if (SessionState.get() != null && SessionState.get().getAuthenticator() != null) {
                    userName = SessionState.get().getAuthenticator().getUserName();
                }
                //如果当前操作用户不是上边的超级管理员,就需要抛出异常,中断本次操作
                if (!admins[0].equalsIgnoreCase(userName)
                        && !admins[1].equalsIgnoreCase(userName)
                        && !admins[2].equalsIgnoreCase(userName)
                        && !admins[3].equalsIgnoreCase(userName)
                        && !admins[4].equalsIgnoreCase(userName)
                        && !admins[5].equalsIgnoreCase(userName)
                ) {
                    throw new SemanticException(userName + " can't use ADMIN options, no permission to perform this operation");
                }
                break;
            default:
                //上边对权限操作的type code 进行了限制,其它操作不受影响
                break;
        }
        return ast;
    }


    public void setConf(Configuration configuration) {
        conf = configuration;
    }

    public Configuration getConf() {
        return conf;
    }
}