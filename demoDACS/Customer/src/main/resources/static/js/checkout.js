fetch('https://xapi.leninn.com/api/cities?language=vi')
    .then(response => response.json())
    .then(data => {
        data.map(city => document.getElementById('city').innerHTML += `<option value="${city.slug}">${city.name}</option>`);
    })
    .catch(error => {
        console.log(error);
    });


//slug
function fetchDistricts(provincesName) {
    fetch(`https://xapi.leninn.com/api/cities/${provincesName}/districts?language=vi`)
        .then(response => response.json())
        .then(data => {
            const districtSelect = document.getElementById('district');
            districtSelect.innerHTML = '<option value="" selected>Select district</option>';
            if (data !== undefined) {

                data.map(value => document.getElementById('district').innerHTML += `<option value='${value._id}'>${value.name}</option>`);
            }
        })
        .catch(error => {
            console.error('Lỗi khi gọi API:', error);
        });
}

//_id
function fetchWards(id) {
    fetch(`https://xapi.leninn.com/api/districts/${id}/communes?language=vi`)
        .then(response => response.json())
        .then(data => {
            const wardSelect = document.getElementById('ward');
            wardSelect.innerHTML = '<option value="" selected>Select ward and commune</option>';
            if (data !== undefined) {
                data.map(value => document.getElementById('ward').innerHTML += `<option value='${value.ma_code}'>${value.name}</option>`);
            }
        })
        .catch(error => {
            console.error('Lỗi khi gọi API:', error);
        });
}

function getDistricts(event) {
    fetchDistricts(event.target.value);
    const wardSelect = document.getElementById('ward');
    wardSelect.innerHTML = '<option value="" selected>Select ward and commune</option>';
}

function getWards(event) {
    fetchWards(event.target.value);
}

