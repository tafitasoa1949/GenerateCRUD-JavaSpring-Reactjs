package com.example.Backend.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;

@Entity
public class Etudiant 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String nom;
	private int age;
	 @ManyToOne
	 @JoinColumn(name = "idposte", nullable = false)
	private Poste poste;

	public void setId( int id )
	{
		this.id=id; 
	}

	public void setNom( String nom )
	{
		this.nom=nom; 
	}

	public void setAge( int age )
	{
		this.age=age; 
	}

	public void setPoste( Poste poste )
	{
		this.poste=poste; 
	}


	public int getId()
	{
		return this.id; 
	}

	public String getNom()
	{
		return this.nom; 
	}

	public int getAge()
	{
		return this.age; 
	}

	public Poste getPoste()
	{
		return poste; 
	}


	public Etudiant(int id, String nom, int age, Poste poste )
	{
		this.setId(id); 
		this.setNom(nom); 
		this.setAge(age); 
		this.setPoste(poste); 
	}
	public Etudiant()
	{

	}
}

