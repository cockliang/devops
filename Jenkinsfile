#!groovy
pipeline {
    //在任何可用的代理上执行Pipeline
    agent any
    //参数化变量，目前只支持[booleanParam, choice, credentials, file, text, password, run, string]这几种参数类型，其他高级参数化类型还需等待社区支持。
    parameters {
        //git代码路径【参数值对外隐藏】
        string(name:'repoUrl', defaultValue: 'http://www.trssh.top/gitlab/gong.jiliang/devops.git', description: 'git代码路径')
        //repoBranch参数后续替换成git parameter不再依赖手工输入,【git parameters目前还不支持pipeline】
        string(name:'repoBranch', defaultValue: 'master', description: 'git分支名称')
        //pom.xml的相对路径
        string(name:'pomPath', defaultValue: 'pom.xml', description: 'pom.xml的相对路径')
        //war包的相对路径
        string(name:'warLocation', defaultValue: 'rpc/war/target/*.war', description: 'war包的相对路径 ')
        //服务器参数采用了组合方式，避免多次选择，使用docker为更佳实践【参数值对外隐藏】
        choice(name: 'server',choices:'192.168.1.107,9090,*****,*****\nserverIP,serverPort,serverName,serverPasswd', description: '测试服务器列表选择(IP,JettyPort,Name,Passwd)')
        //测试服务器的dubbo服务端口
        string(name:'dubboPort', defaultValue: '31100', description: '测试服务器的dubbo服务端口')
        //单元测试代码覆盖率要求，各项目视要求调整参数
        string(name:'lineCoverage', defaultValue: '20', description: '单元测试代码覆盖率要求(%)，小于此值pipeline将会失败！')
        //若勾选在pipelie完成后会邮件通知测试人员进行验收
        booleanParam(name: 'isCommitQA',description: '是否邮件通知测试人员进行人工验收',defaultValue: false )
    }
    //常量参数，初始确定后一般不需更改
    environment{
        //git服务全系统只读账号cred_id【参数值对外隐藏】
        CRED_ID='63e02fe0-01bc-4080-a305-ea6d32546774'
        //测试人员邮箱地址,用于通知人工验收【参数值对外隐藏】
        QA_EMAIL='gong.jiliang@trs.com.cn'
        //接口测试（网络层）的job名，一般由测试人员编写
        ITEST_JOBNAME='Guahao_InterfaceTest_ExpertPatient'
    }
    options {
        //保持构建的最大个数
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    //定期检查开发代码更新，工作日每晚4点做daily build
    /* triggers {
        pollSCM('H 4 * * 1-5')
    }
    */
    //pipeline的各个阶段场景
    stages {
        /*
        stage('代码获取') {
            steps {
                //根据param.server分割获取参数,包括IP,jettyPort,username,password
                script {
                    def split=params.server.split(",")
                    serverIP=split[0]
                    jettyPort=split[1]
                    serverName=split[2]
                    serverPasswd=split[3]
                }
                echo "starting fetchCode from ${params.repoUrl}......"
                // Get some code from a GitHub repository
                git credentialsId:CRED_ID, url:params.repoUrl, branch:params.repoBranch
            }
        }
        */


        stage('单元测试') {
            steps {
                echo "starting unitTest......"
                //注入jacoco插件配置,clean test执行单元测试代码. All tests should pass.
                sh "mvn org.jacoco:jacoco-maven-plugin:prepare-agent -f ${params.pomPath} clean test -Dautoconfig.skip=true -Dmaven.test.skip=false -Dmaven.test.failure.ignore=true"
                // TODO junit '**/target/surefire-reports/*.xml'
                //配置单元测试覆盖率要求，未达到要求pipeline将会fail,code coverage.LineCoverage>20%.
                // TODO jacoco changeBuildStatus: true, maximumLineCoverage:"${params.lineCoverage}"
            }
        }
        stage('静态检查') {
            steps {
                echo "starting codeAnalyze with SonarQube......"
                //sonar:sonar.QualityGate should pass
                /*
                withSonarQubeEnv('SonarQube') {
                    //固定使用项目根目录${basedir}下的pom.xml进行代码检查
                    sh "mvn -f pom.xml clean compile sonar:sonar"
                }
                script {
                    timeout(10) {
                        //利用sonar webhook功能通知pipeline代码检测结果，未通过质量阈，pipeline将会fail
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "未通过Sonarqube的代码质量阈检查，请及时修改！failure: ${qg.status}"
                        }
                    }
                }
                */
            }
        }

        stage('部署测试环境') {
            steps {
                echo "starting deploy to ......"
                //编译和打包
                /*
                sh "mvn  -f ${params.pomPath} clean package -Dautoconfig.skip=true -Dmaven.test.skip=true"
                archiveArtifacts warLocation

                script {
                    wrap([$class: 'BuildUser']) {
                        //发布war包到指定服务器，虚拟机文件目录通过shell脚本初始化建立，所以目录是固定的
                        sh "sshpass -p ${serverPasswd} scp ${params.warLocation} ${serverName}@${serverIP}:htdocs/war"
                        //这里增加了一个小功能，在服务器上记录了基本部署信息，方便多人使用一套环境时问题排查，storge in {WORKSPACE}/deploy.log  & remoteServer:htdocs/war
                        Date date = new Date()
                        def deploylog="${date.toString()},${BUILD_USER} use pipeline  '${JOB_NAME}(${BUILD_NUMBER})' deploy branch ${params.repoBranch} to server ${serverIP}"
                        println deploylog
                        sh "echo ${deploylog} >>${WORKSPACE}/deploy.log"
                        sh "sshpass -p ${serverPasswd} scp ${WORKSPACE}/deploy.log ${serverName}@${serverIP}:htdocs/war"
                        //jetty restart，重启jetty
                        sh "sshpass -p ${serverPasswd} ssh ${serverName}@${serverIP} 'bin/jettyrestart.sh' "
                    }
                }
                */
            }
        }

        stage('接口自动化测试') {
            steps{
                echo "starting interfaceTest......"
                /* script {
                    //为确保jetty启动完成，加了一个判断，确保jetty服务器启动可以访问后再执行接口层测试。
                    timeout(5) {
                        waitUntil {
                            try {
                                //确保jetty服务的端口启动成功
                                sh "nc -z ${serverIP} ${jettyPort}"
                                //sh "wget -q http://${serverIP}:${jettyPort} -O /dev/null"
                                return true
                            } catch (exception) {
                                return false
                            }
                        }
                    }
                    //将参数IP和Port传入到接口测试的job，需要确保接口测试的job参数可注入
                    build job: ITEST_JOBNAME, parameters: [string(name: "dubbourl", value: "${serverIP}:${params.dubboPort}")]
                }
                */
            }
        }

        stage('UI自动化测试') {
            steps{
                echo "starting UITest......"
                //这个项目不需要UI层测试，UI自动化与接口测试的pipeline脚本类似
            }
        }

        stage('性能自动化测试 ') {
            steps{
                echo "starting performanceTest......"
                //视项目需要增加性能的冒烟测试，具体实现后续专文阐述
            }
        }

        stage('通知人工验收'){
            steps{
                echo "通知人工验收......"
                /*
                script{
                    wrap([$class: 'BuildUser']) {
                        if(params.isCommitQA==false){
                            echo "不需要通知测试人员人工验收"
                        }else{
                            //邮件通知测试人员人工验收
                            mail to: "${QA_EMAIL}",
                                    subject: "PineLine '${JOB_NAME}' (${BUILD_NUMBER})人工验收通知",
                                    body: "${BUILD_USER}提交的PineLine '${JOB_NAME}' (${BUILD_NUMBER})进入人工验收环节\n请及时前往${env.BUILD_URL}进行测试验收"
                        }

                    }

                }*/
            }
        }

        stage('发布系统') {
            steps{

                echo "starting deploy......"
                //    TODO发布环节后续专题阐述
            }
        }
    }
}
