import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './Update.css';

const Formulaire = () => {
    const { id } = useParams();
    const [item, setItem] = useState({});
    const [formData, setFormData] = useState({});
    const [columns, setColumns] = useState([]);
	const [ poste, setPoste ] = useState([]);


  const fetchItems = () => {
    let url = "http://localhost:8080/etudiants/"+id;
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
      if (this.readyState === 4) {
        if (this.status === 200) {
          let response = JSON.parse(this.response);
          setItem(response);
        }
      }
    };
    xhttp.open("GET", url, true);
    xhttp.send(null);
};

const fetchDataPoste = () => {
	let url = "http://localhost:8080/postes"
	fetch(url)
	.then((response) => response.json())
	.then((data) => {
		if(data.length > 0) {
			const objectKeys = Object.keys(data[0])
			setColumns(objectKeys)
			setPoste(data)
		}
	})
	.catch((error) => {
		console.error('Error:', error);
	});
}


  useEffect(() => {
    fetchItems();
		fetchDataPoste()

  }, [id]);

    useEffect(() => {
      const objectKeys = Object.keys(item);
      setColumns(objectKeys);
      setFormData(item);
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setItem(prevItem => ({
            ...prevItem,
            [name]: value,
        }));
    };

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

  const handleSubmit = (e) => {
    e.preventDefault();

    let form = document.getElementById("form");
    let formDatas = createFormData(form);

    const xhr = new XMLHttpRequest();
    let url = "http://localhost:8080/etudiants/"+id;

    xhr.open("PUT", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
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
		<input type="text" className="form-control" id="nom" name="nom" placeholder="nom" value={item.nom} onChange={handleChange} />
	</div>
	<div className="form-group">
		<label for="age">age</label>
		<input type="number" className="form-control" id="age" name="age" placeholder="age" value={item.age} onChange={handleChange} />
	</div>
	<div className="form-group">
		<label for="idposte">idposte</label>
		<select className="form-control" id="idposte" name="idposte" value={item.id} onChange={handleChange} >
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
