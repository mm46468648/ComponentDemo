package com.mooc.periodical.model

/**
 * 期刊详情
 * "created_time": "2021-08-12 02:57:22",
"updated_time": "2021-10-13 03:30:19",
"status": 0,
"magid": "320910033311",
"pcurl": "http://qikan.chaoxing.com/mag/infos?mags=f060de13d3de0a3687b9ab838b151dcc&isort=20",
"coverurl": "http://rtt.5read.com/coverNew/CoverNew.dll?iid=7e41df431ed28a195153f38154e2c4c211a8834c85eed7dd4e6bae4b305c36050c05a699f47d7fc2&v=2&p=150x235",
"summary": "《计算机应用》月刊，于1981年创刊，由中国科学院成都计算机应用研究所主办，是国内较早公开发行的计算机技术刊物，在计算机自动化领域有较大影响。《计算机应用》紧紧围绕“应用”，登载应用、开发中的高水平学术技术论文、重大应用成果和典型应用经验。读者对象为各行业、各部门从事计算机应用基础、应用工程、应用软件、应用系统工作的工程技术人员、科研人员和大专院校师生。  《计算机应用》多次荣获全国优秀科技期刊奖、国家期刊奖提名奖，被评为中国期刊方阵双奖期刊、中文核心期刊和中国科技核心期刊。被中国科学引文数据库、中国科技论文统计源数据库等国家重点检索机构列为引文期刊，并被英国《科学文摘》（SA）、俄罗斯《文摘杂志》（AJ）、日本《科学技术文献速报》（JST）、美国《剑桥科学文摘：材料信息》（CSA：MI）、美国《乌利希国际期刊指南》(UIPD)等国际重要检索系统列为来源期刊。  《计算机应用》月刊内容新颖、信息丰富、印刷精美（大16开本，290页），是您学习计算机应用理论，借鉴计算机应用技术，参考计算机应用经验的最佳选择。",
"fenlei_id": 75,
"magname": "计算机应用",
"mags": "f060de13d3de0a3687b9ab838b151dcc",
"choren": 0,
"unit": "中国科学院成都计算机应用研究所",
"sort_id": 1511
 */
class PeriodicalDetail {

    var id : String = ""
    var fenlei_id : String = ""
    var sort_id : String = ""
    var fenleiname : String = ""
    var created_time : String = ""
    var updated_time : String = ""
    var magid : String = ""
    var magname : String = ""
    var unit : String = ""
    var pcurl : String = ""
    var coverurl : String = ""
    var summary : String = ""
    var status : Int =0
    var terms : List<Term>? = null



}

/**
 * 期
 */
class Term{
    var year:Int = 0  //年份  2021
    var value : ArrayList<String> = arrayListOf()  //期数    【"1"，"2"，。。。】
}