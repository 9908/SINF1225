package be.uclouvain.sinf1225.gourmet;
import java.util;

/**
 * @author Alex
 */
// REMARQUES : 
/* faire une table reservationDish avec les plats (+ le nombre souhait�) et le resvId de la r�servation */

public class Reservation
{
	private User user;
	private Restaurant restaurant;
	private int nbrReservtion;
	private List<DishNode> dishes;
	private Date date;
	
	public Reservation(User user, Restaurant restaurant, int nbrReservation, List<DishNode> dishes, Date date)
	{
		this.user = user;
		this. restaurant = restaurant;
		this.nbrReservation = nbrReservation;
		this.dishes = dishes;
		this.date = date;
	}
	
	/* Lecture des variables d'instances*/
	public User getUser() {return this.user;}
	public Restaurant getRestaurant () {return this.restaurant;}
	public int getnbrReservation() {return this.nbrReservation;}
	public List<DishNode> getDish() {return this.dishes;}
	public Date getDate() {return this.date;}
	
	/* Modification des variables d'instances */
	public void setRestaurant (Restaurant resto) {this.restaurant = resto;}
	public void setNbrReservation (int nbr) {this.nbrReservation = nbr;}
	public void setDate (string date) {this.date = date;}
	
	/**
	 * Modification du nombre de plats souhait�s
	 * @param dish plat � modifier
	 * @param nbr nombre souhait�
	 */
	public void setDishNodeNbr (Dish dish, int nbr)
	{
		boolean boucle = true;
		for(int i = 0; i< this.dishes.size() && boucle; i++)
		{
			if(node.dish == dish) 
			{
				node.nbrDishes = nbr;
				boucle = false;
			}
		}
	}
	
	/**
	 * Ajout d'un plat � la r�servation
	 * @param dish plat � ajouter
	 * @param nbr nombre d'exemplaire souhait�
	 */
	public void addDishNode (Dish dish, int nbr) {this.dishes.add(new DishNode(dish, nbr));}
	
	/**
	 * Suppression d'un plat command�
	 * @param dish plat � supprimer de la r�servation
	 */
	public void deleteDishNode (Dish dish)
	{
		int index;
		boolean boucle = true;
		for(int i = 0; i < this.dishes.size() && boucle; i++)
		{
			if(node.dish == dish) 
			{
				index = i;
				boucle = false;
			}
		}
		this.dishes.remove(index);
	}
	
	/**
	 * Structure associant � chaqie plat r�serv�, le nombre "d'exemplaires" souhait�
	 */
	public class DishNode
	{
		public Dish dish;
		public int nbrDishes;
		
		public DishNode (Dish dish, int nbrDishes)
		{
			this.dish = dish;
			this.nbrDishes = nbrDishes;
		}
	}
}
