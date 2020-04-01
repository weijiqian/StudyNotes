执行命令

调用方式:xcall +命令

[/usr/local/bin/xcall.sh]

    #!/bin/bash
    pcount=$@
    if((pcount==0));then
            echo no args;
            exit;
    fi
    
    echo -------------localhost----------
    $@
    for((host=1; host<=3; host=$host + 1)); do
            tput setaf 2
            echo ----------hadoop$host---------
            tput setaf 7
            ssh -4 hadoop$host  "source /etc/profile ; $@"
    done




