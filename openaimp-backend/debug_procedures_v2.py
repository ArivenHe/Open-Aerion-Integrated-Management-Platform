import sqlite3
import os

db_path = r'd:\1-2026-projects\Open-Aerion-Integrated-Management-Platform\openaimp-backend\src\main\resources\little_navmap_navigraph.sqlite'

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    print("\n--- Searching for 'ELKUR' in transition or approach_leg for ZBAA ---")
    # Check if ELKUR exists in transition or approach_leg related to ZBAA SIDs
    # Query approach_leg
    cursor.execute("""
        SELECT DISTINCT a.arinc_name, al.fix_ident 
        FROM approach a 
        JOIN approach_leg al ON a.approach_id = al.approach_id 
        WHERE a.airport_ident = 'ZBAA' AND a.suffix = 'D' AND al.fix_ident = 'ELKUR'
    """)
    rows = cursor.fetchall()
    print("In approach_leg:")
    for row in rows:
        print(row)
        
    # Query transition
    cursor.execute("""
        SELECT DISTINCT a.arinc_name, t.fix_ident 
        FROM approach a 
        JOIN transition t ON a.approach_id = t.approach_id 
        WHERE a.airport_ident = 'ZBAA' AND a.suffix = 'D' AND t.fix_ident = 'ELKUR'
    """)
    rows = cursor.fetchall()
    print("In transition:")
    for row in rows:
        print(row)

    print("\n--- Searching for 'PK' in transition or approach_leg for ZSPD ---")
    # Query approach_leg
    cursor.execute("""
        SELECT DISTINCT a.arinc_name, al.fix_ident 
        FROM approach a 
        JOIN approach_leg al ON a.approach_id = al.approach_id 
        WHERE a.airport_ident = 'ZSPD' AND a.suffix = 'A' AND al.fix_ident = 'PK'
    """)
    rows = cursor.fetchall()
    print("In approach_leg:")
    for row in rows:
        print(row)
        
    # Query transition
    cursor.execute("""
        SELECT DISTINCT a.arinc_name, t.fix_ident 
        FROM approach a 
        JOIN transition t ON a.approach_id = t.approach_id 
        WHERE a.airport_ident = 'ZSPD' AND a.suffix = 'A' AND t.fix_ident = 'PK'
    """)
    rows = cursor.fetchall()
    print("In transition:")
    for row in rows:
        print(row)

    conn.close()

except Exception as e:
    print(f"Database error: {e}")
