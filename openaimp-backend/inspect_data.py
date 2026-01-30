import sqlite3
import os

db_path = r'd:\1-2026-projects\Open-Aerion-Integrated-Management-Platform\openaimp-backend\src\main\resources\little_navmap_navigraph.sqlite'

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # Inspect runway_end
    print("\n--- Schema for runway_end ---")
    cursor.execute("PRAGMA table_info(runway_end)")
    columns = cursor.fetchall()
    for col in columns:
        print(f"{col[1]} ({col[2]})")

    # Sample data from approach to understand procedure names
    print("\n--- Sample Approach Data (SIDs/STARs) ---")
    # suffix 'D' for SID, 'A' for STAR
    cursor.execute("SELECT arinc_name, type, suffix, runway_name, fix_ident FROM approach WHERE suffix IN ('D', 'A') LIMIT 10")
    rows = cursor.fetchall()
    for row in rows:
        print(row)

    # Sample data from runway_end
    print("\n--- Sample Runway End Data ---")
    cursor.execute("SELECT * FROM runway_end LIMIT 5")
    rows = cursor.fetchall()
    for row in rows:
        print(row)
        
    conn.close()

except Exception as e:
    print(f"Database error: {e}")
