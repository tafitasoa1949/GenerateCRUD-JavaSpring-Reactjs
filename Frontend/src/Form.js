import React, { useState, useEffect } from 'react';
import './Form.css';

const Formulaire = () => {
  const [formData, setFormData] = useState({});
  const [columns, setColumns] = useState([]);
	const [ poste, setPoste ] = useState([]);


const fetchItems = () => {
	let url = "http://localhost:8080/postes"
	fetch(url)
	.then((response) => response.json())
	.then((data) => {
		if(data.length > 0) {
			const objectKeys = Object.keys(data[0])
			setPoste(data)
		}
	})
	.catch((error) => {
		console.error('Error:', error);
	});
}

useEffect(() => {
	fetchItems()
}, []);


  const createFormData = (form) => {
      let formData = new FormData(form);
      let object = {
		nom: formData.get('nom'),
		age: formData.get('age'),
		poste: {
			id: formData.get('idposte'),
		},

      };
      return object;
    }

  const handleChange = (e, columnName) => {
    setFormData({
      ...formData,
      [columnName]: e.target.value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    let form = document.getElementById("form");
    let formDatas = createFormData(form);

    const xhr = new XMLHttpRequest();
    let url = "http://localhost:8080/etudiants";

    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 201) {
                window.location.href = "/";
            } else {
                console.error('Error adding object');
            }
        }
    };

    xhr.send(JSON.stringify(formDatas));
};

  return (
  <>
  <form onSubmit={handleSubmit} id="form">
	<div className="form-group">
		<label for="nom">nom</label>
		<input type="text" className="form-control" id="nom" name="nom" placeholder="nom" />
	</div>
	<div className="form-group">
		<label for="age">age</label>
		<input type="number" className="form-control" id="age" name="age" placeholder="age" />
	</div>
	<div className="form-group">
		<label for="idposte">idposte</label>
		<select className="form-control" id="idposte" name="idposte">
		{poste.map((item) => (
			<option value={item.id}>{item.nom}</option>
		))}
		</select>
	</div>
     <button type="submit">Submit</button>
  </form>
  </>
);
};

export default Formulaire;
