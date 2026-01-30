import sqlite3
import os

db_path = r'd:\1-2026-projects\Open-Aerion-Integrated-Management-Platform\openaimp-backend\src\main\resources\little_navmap_navigraph.sqlite'

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    print("\n--- ZBAA SIDs with fix_ident = 'ELKUR' ---")
    cursor.execute("SELECT arinc_name, fix_ident, runway_name, type FROM approach WHERE airport_ident = 'ZBAA' AND suffix = 'D' AND fix_ident = 'ELKUR'")
    rows = cursor.fetchall()
    if not rows:
        print("No SIDs found for ELKUR")
        print("Checking distinct fix_idents for ZBAA SIDs:")
        cursor.execute("SELECT DISTINCT fix_ident FROM approach WHERE airport_ident = 'ZBAA' AND suffix = 'D' LIMIT 10")
        for row in cursor.fetchall():
            print(row)
    else:
        for row in rows:
            print(row)
            
    print("\n--- ZSPD STARs with fix_ident = 'PK' ---")
    cursor.execute("SELECT arinc_name, fix_ident, runway_name, type FROM approach WHERE airport_ident = 'ZSPD' AND suffix = 'A' AND fix_ident = 'PK'")
    rows = cursor.fetchall()
    if not rows:
        print("No STARs found for PK")
        print("Checking distinct fix_idents for ZSPD STARs:")
        cursor.execute("SELECT DISTINCT fix_ident FROM approach WHERE airport_ident = 'ZSPD' AND suffix = 'A' LIMIT 10")
        for row in cursor.fetchall():
            print(row)
    else:
        for row in rows:
            print(row)

    conn.close()

except Exception as e:
    print(f"Database error: {e}")
