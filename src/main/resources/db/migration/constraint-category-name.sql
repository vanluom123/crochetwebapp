-- Add a unique constraint on the 'category_name' column
ALTER TABLE product_category
ADD CONSTRAINT category_name_constraint UNIQUE (category_name);