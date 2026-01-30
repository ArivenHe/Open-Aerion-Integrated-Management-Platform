import sqlite3
import os

db_path = r'd:\1-2026-projects\Open-Aerion-Integrated-Management-Platform\openaimp-backend\src\main\resources\little_navmap_navigraph.sqlite'

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # Check ZBAA procedures
    print("\n--- ZBAA SIDs (suffix='D') ---")
    cursor.execute("SELECT arinc_name, fix_ident, runway_name, type FROM approach WHERE airport_ident = 'ZBAA' AND suffix = 'D' LIMIT 20")
    rows = cursor.fetchall()
    for row in rows:
        print(row)
        
    print("\n--- ZBAA STARs (suffix='A') ---")
    cursor.execute("SELECT arinc_name, fix_ident, runway_name, type FROM approach WHERE airport_ident = 'ZBAA' AND suffix = 'A' LIMIT 20")
    rows = cursor.fetchall()
    for row in rows:
        print(row)

    conn.close()

except Exception as e:
    print(f"Database error: {e}")
