import pandas as pd
import os

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))  # knocksea 폴더 기준
FOLDER_PATH = os.path.join(BASE_DIR, "sql", "mongodb","fishingSpots_excels")
OUTPUT_CSV = os.path.join(BASE_DIR, "sql", "mongodb", "fishing_spot.csv")
# OUTPUT_EXCEL="fishing_spot_excel.xlsx" #필요하면 주석 해제

TARGET_COLUMNS = ['낚시터명','낚시터유형','소재지도로명주소',
                  'WGS84위도','WGS84경도','주요어종','이용요금','데이터기준일자'
]

NUMERIC_COLUMNS = ['WGS84위도','WGS84경도']

def merge_excels_to_csv(folder_path, output_csv):

    excel_files = [f for f in os.listdir(folder_path) if f.endswith(('.xlsx', '.xls'))]
    merged_df = pd.DataFrame()

    for file in excel_files:
        file_path = os.path.join(folder_path, file)
        try:
            df = pd.read_excel(file_path)
            df = df[TARGET_COLUMNS]
            for col in NUMERIC_COLUMNS:
                df[col] = pd.to_numeric(df[col], errors='coerce')
            merged_df = pd.concat([merged_df, df], ignore_index=True)
        except Exception as e:
            print(f"파일 '{file}' 처리 중 오류 발생: {e}")

    merged_df.to_csv(output_csv, index=False, encoding='utf-8-sig')
    # merged_df.to_excel(OUTPUT_EXCEL, index=False, engine='openpyxl')  # 필요시 주석 해제
    print(f"모든 엑셀 파일을 병합하여 '{output_csv}'로 저장했습니다.")

if __name__ == "__main__":
    merge_excels_to_csv(FOLDER_PATH, OUTPUT_CSV)