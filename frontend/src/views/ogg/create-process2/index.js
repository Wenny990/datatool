import {useRepoApi} from "/@/api/repo";

const {getRepo} = useRepoApi();

let respList = [];
getRepo().then(resp => {
    respList = resp.data
})


export const initOggGenerateConfig = (oggGenerateConfig) => {
    let processName = oggGenerateConfig.processName;
    const windowsSplitChat = "\\";
    const linuxSplitChat = "/";

    const splitChar = oggGenerateConfig.sourceServer.serverOs === '01' ? windowsSplitChat : linuxSplitChat;
    const splitChar1 = oggGenerateConfig.targetServer.serverOs === '01' ? windowsSplitChat : linuxSplitChat;

    oggGenerateConfig.sourceTrailPath = '.' + splitChar + 'dirdat' + splitChar  + processName + splitChar + 'e' + processName[processName.length - 1];
    oggGenerateConfig.targetTrailPath = '.' + splitChar1 + 'dirdat' + splitChar1 + processName + splitChar1 + 't' + processName[processName.length - 1];
    oggGenerateConfig.targetRinTrailPath = '.' + splitChar1 + 'dirdat' + splitChar1 + processName + splitChar1 + 'r' + processName[processName.length - 1];

    oggGenerateConfig.megaBytes = 100
    oggGenerateConfig.extSeqNo = 0
    oggGenerateConfig.extRba = 0
    oggGenerateConfig.allCol = false
    oggGenerateConfig.format = false
    oggGenerateConfig.formatRelease = '12.3'



    oggGenerateConfig.addCheckPointTable = true
    oggGenerateConfig.checkPointTable = oggGenerateConfig.targetDbSchema + '.ckp';

    const sourceRepo = respList.find(t => t.id === oggGenerateConfig.sourceServer?.repositorySourceId)
    const targetRepo = respList.find(t => t.id === oggGenerateConfig.targetServer?.repositorySourceId)
    oggGenerateConfig.sourceRepo = sourceRepo;

    oggGenerateConfig.targetRepo = targetRepo;

    oggGenerateConfig.extName = 'EXT' + processName;

    oggGenerateConfig.pumName = 'PUM' + processName;

    oggGenerateConfig.repName = 'REP' + processName;
    oggGenerateConfig.einName = 'EIN' + processName;
    oggGenerateConfig.rinName = 'RIN' + processName;

    oggGenerateConfig.isInit = true;
    oggGenerateConfig.isRange = false;
    oggGenerateConfig.rinRange = 5;

    if(sourceRepo.dataProviderType != '2'){
        oggGenerateConfig.addTranData = true;
    }else{
        oggGenerateConfig.addTranData = false;
    }

    oggGenerateConfig.createSubdirs = true;

    //MYSQL配置
    if(sourceRepo.dataProviderType == '2'){
        oggGenerateConfig.mysqlHome = '/var/lib/mysql';
        oggGenerateConfig.tranlogOptions = '/var/lib/mysql/mysql-bin.index';
        oggGenerateConfig.dbOptions = 'host ' + sourceRepo.host + ',connectionport ' + sourceRepo.port;
    }
    if(sourceRepo.dataProviderType == '3'){
        oggGenerateConfig.sourceNlsLang = 'AMERICAN_AMERICA.ZHS16GBK'
    }
    if(targetRepo.dataProviderType == '3'){
        oggGenerateConfig.targetNlsLang = 'AMERICAN_AMERICA.ZHS16GBK'
    }


}