// test passing buget data to servlet

on
var json=[1,2,3,4];
$.ajax({
            url:"\Ajax",
            type:"POST",
            dataType:'json',
            data: {json:json},
            success:function(data){
              alert(data);
            }
        });


