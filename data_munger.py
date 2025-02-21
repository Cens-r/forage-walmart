import pandas as pd
from pathlib import Path
import sqlite3

DATA_DIRECTORY = Path("data")
DATABASE_FILE = Path("shipment_database.db")

db_conn = sqlite3.connect(DATABASE_FILE)

# Clean tables
for table in ["product", "shipment"]:
    db_conn.execute(f"DELETE FROM {table}")
db_conn.commit()

def get_product_id(product_name: str) -> int:
    cursor = db_conn.cursor()

    # Check for existing product
    cursor.execute("""
        SELECT * FROM product
        WHERE name = (?)
    """, (product_name,))
    product = cursor.fetchone()

    if product: # Return id early if exists
        return product[0]

    # Create new product
    cursor.execute("""
        INSERT INTO product
        (name)
        VALUES (?)
    """, (product_name,))
    db_conn.commit()

    return cursor.lastrowid

def save_shipments(shipment_data):
    shipment_data["product_id"] = shipment_data["product"].apply(get_product_id)
    shipment_records = shipment_data[["product_id", "quantity", "origin", "destination"]].values.tolist()

    db_conn.executemany("""
            INSERT INTO shipment (product_id, quantity, origin, destination)
            VALUES (?, ?, ?, ?)
        """, shipment_records)
    db_conn.commit()

def format_shipment_data(shipment_data):
    shipment_data = shipment_data.rename(columns={
        "product_quantity": "quantity",
        "origin_warehouse": "origin",
        "destination_store": "destination"
    })
    return shipment_data

# Process and Save shipment_0
shipment_0_raw = pd.read_csv(DATA_DIRECTORY / "shipping_data_0.csv")
shipment_0_formatted = format_shipment_data(shipment_0_raw)
save_shipments(shipment_0_formatted)

# Process and Save shipment_1 and shipment_2
shipment_1_raw = pd.read_csv(DATA_DIRECTORY / "shipping_data_1.csv")
shipment_2_raw = pd.read_csv(DATA_DIRECTORY / "shipping_data_2.csv")

shipment_combined_raw = shipment_2_raw.merge(shipment_1_raw, on="shipment_identifier")
shipment_combined_raw = shipment_combined_raw.groupby([
    "shipment_identifier",
    "product",
    "on_time",
    "origin_warehouse",
    "destination_store",
    "driver_identifier"
]).value_counts().reset_index(name="product_quantity")

shipment_combined_formatted = format_shipment_data(shipment_combined_raw)
save_shipments(shipment_combined_formatted)

db_conn.close()