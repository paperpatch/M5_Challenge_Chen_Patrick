use game_store_catalog;

insert into game(title, esrb_rating, description, price, studio, quantity)
	values ('World of Goo', 'PG', 'puzzle video game', 12.99, '2D Boy', 25);

insert into console(model, manufacturer, memory_amount, processor, price, quantity)
	values ('Nintendo Switch', 'Nintendo', '4GB', 'ARM 4 Cortex-A57', 299.99, 7);
    
insert into tshirt (size, color, description, price, quantity)
	values ('Med', 'Yellow', 'Science', 14.95, 0);