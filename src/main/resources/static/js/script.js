console.log("this is script file")

    function toggleSidebar() {
	   // true
	   //band karna hai
        if ($(".sidebar").is(":visible")) {
            $(".sidebar").hide();
            $(".content").css("margin-left", "0%");
        } else {
	     
	     //false
	     //show karna hai
            $(".sidebar").show();
            $(".content").css("margin-left", "20%");
        }
    }
    
    
  const search = () => {
    //console.log("searching...")
    
    let query = $("#search-input").val();
    if (query == "") {
        $(".search-result").hide(); 
    } else {
        //search
        console.log(query);
        
        //sending request to server
        
        let url = `http://localhost:8383/search/${query}`; 
        fetch(url)
        .then((response) => {
            return response.json();
        })
        .then((data) => {
            
            //data...
            //console.log(data);
            
            let text = '<div class="list-group">'; 
           
           
                data.forEach((contact) => {
                    text += `<a href="/user/${contact.cId}/contact" class="list-group-item list-group-item-action"> ${contact.name} </a>`; // Fixed the inconsistency in quotation marks and class attribute
                });
                
                text += '</div>';
                
                $(".search-result").html(text); 
                $(".search-result").show();
            
        });
    }
}





