@echo off
title Lisvus Account Manager Console
@java -Djava.util.logging.config.file=console.cfg -cp ./../libs/*;L2JLisvus.jar net.sf.l2j.accountmanager.SQLAccountManager
@pause
