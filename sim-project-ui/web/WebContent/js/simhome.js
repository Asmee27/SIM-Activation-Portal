/**
 * 
 * 
 */

// Function to show snack bar
function snack(str) {
    // Get the snackbar DIV
    var x = document.getElementById("snackbar")
    x.innerHTML = str;
    // Add the "show" class to DIV
    x.className = "show";
    // After 3 seconds, remove the show class from DIV
    setTimeout(function() {
        x.className = x.className.replace("show", "");
    }, 3000);
}

// Extracts the backend's {"message": "..."} from a failed AJAX call.
// Falls back to a generic text when the gateway/service could not be reached.
function errorMessage(error) {
    if (error && error.responseJSON && error.responseJSON.message) {
        return error.responseJSON.message;
    }
    return "Service unavailable. Please try again later.";
}

// Function to activate button on basic details page
function activateButton1(element) {

    if (element.checked) {
        document.getElementById("basicbtn").disabled = false;
    } else {
        document.getElementById("basicbtn").disabled = true;
    }

}

// Function to activate button for terms and conditions
function activateButton2(element) {

    if (element.checked) {
        document.getElementById("termsbtn").disabled = false;
    } else {
        document.getElementById("termsbtn").disabled = true;
    }

}

var rootUrl = "http://localhost:8181"
var simServiceUrl = rootUrl + "/sim/validation";
var offersUrl = rootUrl + "/sim/offer";
var basicUrl = rootUrl + "/customer/validation";
var detailsUrl = rootUrl + "/customer/details";
var idProofUrl = rootUrl + "/idproof/validation";
var customerUrl = rootUrl + "/customer/activate"
var activateUrl = rootUrl + "/sim/activation";
var loginUrl = rootUrl + "/auth/login";

// ---------- Authentication (JWT issued by the API gateway) ----------
// The token lives in sessionStorage: it is cleared when the tab is closed.
function getToken() {
    try { return sessionStorage.getItem('token'); } catch (e) { return null; }
}
function setToken(token) {
    try {
        if (token) { sessionStorage.setItem('token', token); } else { sessionStorage.removeItem('token'); }
    } catch (e) { /* storage unavailable: user will be asked to log in again */ }
}
function showLogin() {
    $('#loginError').text('');
    $('#loginModal').modal('show');
}

// Attach the token to every call made to OUR gateway only (never to third parties like Google).
$.ajaxSetup({
    beforeSend: function(xhr, settings) {
        var token = getToken();
        if (token && settings.url.indexOf(rootUrl) === 0) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + token);
        }
    }
});

// Any 401 from the gateway (missing/expired token) -> ask the user to log in again.
$(document).ajaxError(function(event, xhr, settings) {
    if (xhr.status === 401 && settings.url !== loginUrl) {
        setToken(null);
        showLogin();
    }
});

$(document).ready(function() {
    if (!getToken()) {
        showLogin();
    }

    $('#loginForm').submit(function(event) {
        $.ajax({
            url: loginUrl,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                'username': $('#username').val(),
                'password': $('#password').val()
            }),
            success: function(response) {
                setToken(response.token);
                $('#password').val('');
                $('#loginModal').modal('hide');
            },
            error: function(error) {
                $('#loginError').text(errorMessage(error));
            }
        });
    });

    // Validating SIM and Service Details
	// Sends the SIM and Service number
	// Returns success or error
	
    $('#pg1btn').click(
        function(event) {
            var simnum1 = $('#simNum1').val();
            var sernum1 = $('#servNum1').val();
            // console.log('sim' + simnum1 + 'ser' + sernum1);
            jsondata = {
            		'simNumber':simnum1,
            		'serviceNumber':sernum1
            };
            console.log(jsondata);
            $.ajax({
        		url:simServiceUrl,
        		type:'POST',
        		data:JSON.stringify(jsondata),
        		contentType:"application/json",
        		headers:{contentType:"application/x-www-form-urlencoded;charset=UTF-8"},
        		success : function(response, status, xhr) {
        			if (response) {
        				console.log("SUCCESS" + JSON.stringify(response));
        				localStorage.setItem('simNumber', simnum1);
        				localStorage.setItem('serviceNumber', sernum1);
        				snack(response.message);
        				loadOffer(simnum1);
                        $('#carousel-example-generic').carousel('next');
        			}
        		},
        		error : function(error) {
        			console.log(errorMessage(error));
        			snack(errorMessage(error));
        		}
        	});
    });

    // Fetch the offer only after the SIM has been validated
    function loadOffer(simnum1) {
            $.getJSON({
                url: offersUrl + "/" + simnum1,
                contentType:"application/json",
                headers:{contentType:"application/x-www-form-urlencoded;charset=UTF-8"},
                success: function(data) {
                    if(data) {
                    	console.log(data);
                    	 $("#offer").html(data.offerName + "<br/><br/>" +
                    			 "Get " + data.callQty + " minutes calling,<br/>" + 
                    			 data.dataQty + " MB 4G data <br/>for " + 
                    			 data.duration + " days<br><br>" + 
                    			 "at just Rs. " + data.cost);
                    	 localStorage.setItem('offerId', data.offerId);
                    	 localStorage.setItem('offerName', data.offerName);

                    }
                    else {
                    	snack("Some unknown error occurred");
                    }
                   
                },
                error: function(error) {
                    snack(errorMessage(error));
                }
            });
    }

    // Action on clicking offers button
    // Carousel moves to Basic User Validation
    $("#offbtn").click(function(event) {
        $('#carousel-example-generic').carousel('next');
    });

    // Basic user validation button click action
    // Verifies email and DOB
    $("#basicbtn").click(function(event) {
        var email1 = $('#email1').val();
        var dob1 = $('#dob1').val();
        console.log(email1);
        console.log(dob1);
        jsondata = {
        		'email':email1,
        		'dob':dob1
        };
        $.ajax({
    		url:basicUrl,
    		type:'POST',
    		data:JSON.stringify(jsondata),
    		contentType:"application/json",
    		headers:{contentType:"application/x-www-form-urlencoded;charset=UTF-8"},
    		success : function(response, status, xhr) {
    			if (response) {
    				console.log("SUCCESS" + JSON.stringify(response));
    		        localStorage.setItem('email', email1);
    		        localStorage.setItem('dob', dob1);
    		        $('#dob2').val(dob1);
    		        $('#email2').val(email1);
                    $('#carousel-example-generic').carousel('next');
    			}
    		},
    		error : function(error) {
    			console.log(errorMessage(error));
    			snack(errorMessage(error));
    		}
    	});
    });

    // On Personal Details page
    // Confirm email is checked in the browser; names are validated by customer-service (US05)
    $("#persbtn").click(function(event) {
        var title1 = $('#title1').val();
        var fname1 = $('#fname1').val();
        var lname1 = $('#lname1').val();
        var cemail = $('#cemail2').val();

        if (localStorage.getItem('email') != cemail) {
        	snack("Please check the details entered again.");
        	return;
        }
        jsondata = {
        		'email':cemail,
        		'firstName':fname1,
        		'lastName':lname1
        };
        $.ajax({
    		url:detailsUrl,
    		type:'POST',
    		data:JSON.stringify(jsondata),
    		contentType:"application/json",
    		success : function(response, status, xhr) {
    			localStorage.setItem('title1', title1);
    			localStorage.setItem('lname', lname1);
    			localStorage.setItem('fname', fname1);
    			$('#carousel-example-generic').carousel('next');
    		},
    		error : function(error) {
    			snack(errorMessage(error));
    		}
    	});
    });

    
	 $('#pin11').change(function(event) { 
		 	pin1 = $("#pin11").val(); 
		 	url1 = "http://maps.googleapis.com/maps/api/geocode/json?address="+pin1+"&region=in"; 
		 	console.log(url1);
		 	$.getJSON({
		 		url : url1, 
			 	success : function(data) { 
			 		console.log(data);
			 		data1 = data;
			 		po = data1['results'][0]['address_components'];
			 		var city = po[1]['long_name'];
			 		var state = po[3]['long_name'];
			 		localStorage.setItem('state', state);
			 		console.log(city + state); 
			 		// document.getElementById('title1').value = state;
			 		$("#state1 option[value='"+state+"']").prop('selected', 'selected');

			 		$("#city1").val(city); 
			 		
		 		}, 
		 		error : function(error) {
		 			console.log('error in getting pin code data')
 				}
	 		});
	 	});
	 

    // Action on clicking Address Details page button
    $("#addrbtn").click(function(event) {
        add1 = $("#add1").val();
        state1 = $("#state1").val();
        city1 = $("#city1").val();
        pin1 = $("#pin11").val();

        
        localStorage.setItem('address', (add1));
        localStorage.setItem('city', (city1));
        localStorage.setItem('pin', (pin1));
        localStorage.setItem('state', (state1));

 		$("#state2").val(state1);
        $("#fname2").val(localStorage.getItem('fname'));
        $("#lname2").val(localStorage.getItem('lname'));
        $("#dob3").val(localStorage.getItem('dob'));
        if(add1 != "" && city1 != "" && pin1 != null)
        	$('#carousel-example-generic').carousel('next');
        $("#ffname").html(localStorage.getItem('fname'));
        $("#flname").html(localStorage.getItem('lname'));
        $("#femail").html(localStorage.getItem('email'));
        $("#fdob").html(localStorage.getItem('dob'));
        $("#foffer").html(localStorage.getItem('offerName'));
        $("#fservice").html(localStorage.getItem('serviceNumber'));
    });


    $("#idbtn").click(function(event) {
        state1 = $("#state2").val();
        idn = $("#idn1").val();
        fname2 = $("#fname2").val();
        lname2 = $("#lname2").val();
        idtype = $("#id1").val();
        dob = $("#dob3").val();

        //localStorage.setItem('state1', (state1));
        localStorage.setItem('idn', (idn));
        //localStorage.setItem('fname2', (fname2));
        //localStorage.setItem('lname2', (lname2));
		if(localStorage.getItem('fname') == fname2 
				&& localStorage.getItem('lname') == lname2
				&& localStorage.getItem('state') == state1) {
	        jsondata = {
	        		'idType':idtype,
	        		'idNumber':idn,
	        		'firstName':fname2,
	        		'lastName':lname2,
	        		'state':state1,
	        		'dob':dob
	        }
	        console.log("data" + JSON.stringify(jsondata));
			$.ajax({
	    		url:idProofUrl,
	    		type:'POST',
	    		data:JSON.stringify(jsondata),
	    		contentType:"application/json",
	    		headers:{contentType:"application/x-www-form-urlencoded;charset=UTF-8"},
	    		success : function(response, status, xhr) {
	    			if (response) {
	    				console.log("SUCCESS" + JSON.stringify(response));
	                    $('#carousel-example-generic').carousel('next');
	    			}
	    		},
	    		error : function(error) {
	    			console.log("FAILURE" + JSON.stringify(error));
	    			snack(errorMessage(error));
	    		}
	    	});

		}
		else {
			snack("Wrong details entered.");
		}


    });

    $("#termsbtn").click(function(event) {
        jsondata = {
        		'firstName':localStorage.getItem('fname'),
        		'lastName':localStorage.getItem('lname'),
        		'email':localStorage.getItem('email'),
        		'dob':localStorage.getItem('dob'),
        		'address':{
        			'address':localStorage.getItem('address'),
        			'pinCode':localStorage.getItem('pin'),
        			'city':localStorage.getItem('city'),
        			'state':localStorage.getItem('state')
        		},
        		'simNumber': localStorage.getItem('simNumber')
        };
        console.log(jsondata);
        $.ajax({
    		url:customerUrl,
    		type:'POST',
    		data:JSON.stringify(jsondata),
    		contentType:"application/json",
    		headers:{contentType:"application/x-www-form-urlencoded;charset=UTF-8"},
    		success : function(response, status, xhr) {
    			if (response) {
    				console.log("SUCCESS" + JSON.stringify(response));
    				snack(response.message);
    			}
    			$('#carousel-example-generic').carousel('next');
    		},
    		error : function(error) {
    			console.log(errorMessage(error));
    			snack(errorMessage(error));
    		}
    	});

    });


});