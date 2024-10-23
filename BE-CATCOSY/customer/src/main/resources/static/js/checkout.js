fetch('https://leninn.com/checkout/addresses.json')
    .then(response => response.json())
    .then(data => {
        const haveCodeCity = $('#haveCodeCity').val();

        data.provinces.map(city => {
            if (haveCodeCity === city.code) document.getElementById('city').innerHTML += `<option value="${city.code}" selected>${city.name}</option>`;
            else document.getElementById('city').innerHTML += `<option value="${city.code}">${city.name}</option>`
        });
    })
    .catch(error => {
        console.log(error);
    });


//slug
function fetchDistricts(provincesName) {
    fetch('https://leninn.com/checkout/addresses.json')
        .then(response => response.json())
        .then(data => {
            const districtSelect = document.getElementById('district');
            const haveCodeDistrict = $('#haveCodeDistrict').val();
            if(haveCodeDistrict.length > 0) districtSelect.innerHTML = '<option value="" >Select district</option>';
            else districtSelect.innerHTML = '<option value="" selected>Select district</option>';
            if (data !== undefined) {
                var idProvince = $('#city').val();
                data.districts.map(value => {
                    if (value.province_id == idProvince) {
                        document.getElementById('district').innerHTML += `<option value='${value.code}'>${value.name}</option>`
                    }
                });
            }
        })
        .catch(error => {
            console.error('Lỗi khi gọi API:', error);
        });
}

//_id
function fetchWards(id) {
    fetch('https://leninn.com/checkout/addresses.json')
        .then(response => response.json())
        .then(data => {
            const wardSelect = document.getElementById('ward');
            wardSelect.innerHTML = '<option value="" selected>Select ward and commune</option>';
            var idProvince = $('#city').val();
            var idDistrict = $('#district').val();

            if (data !== undefined) {
                data.wards.map(value => {
                    if (value.province_id == idProvince && value.district_id == idDistrict) {
                        document.getElementById('ward').innerHTML += `<option value='${value.code}'>${value.name}</option>`
                    }
                })
                ;
            }
        })
        .catch(error => {
            console.error('Lỗi khi gọi API:', error);
        });
}

function getDistricts(event, load) {
    fetchDistricts(event.target.value);
    if (!load) {
        const wardSelect = document.getElementById('ward');
        wardSelect.innerHTML = '<option value="" selected>Select ward and commune</option>';
    }
}

function getWards(event) {
    fetchWards(event.target.value);
}


function showInfoBank() {

    const checkBank = document.querySelector('#pay-bank');
    const frameBank = document.querySelector('.my-bank');

    if (checkBank.checked) {
        frameBank.style.height = 'auto';
    } else {
        frameBank.style.height = '0';
    }

}

