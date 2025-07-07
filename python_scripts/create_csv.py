import pandas as pd
import os

BASE_DIR = os.path.dirname(
    os.path.dirname(os.path.abspath(__file__)))  # knocksea 폴더 기준
FOLDER_PATH = os.path.join(BASE_DIR, "sql", "mongodb", "fishingSpots_excels")
OUTPUT_CSV = os.path.join(BASE_DIR, "sql", "mongodb", "fishing_spot.csv")
# OUTPUT_EXCEL=os.path.join(BASE_DIR, "sql", "mongodb", "fishing_spot_excel.xlsx" )

TARGET_COLUMNS = ['낚시터명', '낚시터유형', '주소',
                  'WGS84위도', 'WGS84경도', '전화번호', '주요어종', '이용요금', '데이터기준일자'
                  ]

NUMERIC_COLUMNS = ['WGS84위도', 'WGS84경도']

# 시도 줄임말 -> 정식 행정구역명
SIDO_CORRECTIONS = {
  '충남': '충청남도',
  '대전': '대전광역시',
  '경남': '경상남도',
  '세종시': '세종특별자치시',
  '김포시': '경기도 김포시'
}


def correct_sido(address):
  """주소 앞부분의 시/도를 정식 명칭으로 수정"""
  if not isinstance(address, str):
    return address
  for wrong, correct in SIDO_CORRECTIONS.items():
    if address.startswith(wrong + " ") or address == wrong:
      return address.replace(wrong, correct, 1)
  return address


def merge_excels_to_csv(folder_path, output_csv):
  excel_files = [f for f in os.listdir(folder_path) if
                 f.endswith(('.xlsx', '.xls'))]
  merged_df = pd.DataFrame()

  for file in excel_files:
    file_path = os.path.join(folder_path, file)
    try:
      df = pd.read_excel(file_path)
      # 주소 처리: 소재지도로명주소 > 소재지지번주소 우선순위
      road_col = df['소재지도로명주소'] if '소재지도로명주소' in df.columns else pd.Series(
          [pd.NA] * len(df))
      lot_col = df['소재지지번주소'] if '소재지지번주소' in df.columns else pd.Series(
          [pd.NA] * len(df))

      df['주소'] = road_col.fillna('').astype(str).str.strip()  # 도로명주소 우선
      df['주소'] = df['주소'].replace('', pd.NA)  # 도로명주소가 빈 값인 경우 NA
      df['주소'] = df['주소'].combine_first(lot_col)  # NA면 지번주소로 대체

      # 주소 앞부분 잘못된 시/도 정정
      df['주소'] = df['주소'].astype(str).apply(correct_sido)

      # 전화번호 처리: 없으면 NaN으로 채움
      if '낚시터전화번호' in df.columns:
        df['전화번호'] = df['낚시터전화번호'].replace('', pd.NA)
        df.drop(columns=['낚시터전화번호'], inplace=True)
      else:
        df['전화번호'] = pd.NA

      # 시/도 및 시군구 추출
      df['주소'] = df['주소'].fillna('').astype(str)
      df[['시/도', '시군구(전체)']] = df['주소'].str.extract(r'^(\S+)\s+(\S+)', expand=True)

      # 시/도 정정 반영
      # df['시/도'] = df['시/도'].apply(lambda x: SIDO_CORRECTIONS.get(x, x))


      # 김포시 특수 처리
      df.loc[df['시군구(전체)'] == '김포시', '시/도'] = '경기도'
      df.loc[df['시군구(전체)'] == '김포시', '시군구(전체)'] = '김포시'

      # 필요한 컬럼만 추출 (존재하는 것만)
      final_columns = TARGET_COLUMNS + ['시/도', '시군구(전체)']
      df = df[[col for col in final_columns if col in df.columns]]

      # 필요한 컬럼만 추출 (존재하는 컬럼만)
      # df = df[[col for col in TARGET_COLUMNS if col in df.columns]]

      for col in NUMERIC_COLUMNS:
        df[col] = pd.to_numeric(df[col], errors='coerce')
      merged_df = pd.concat([merged_df, df], ignore_index=True)
    except Exception as e:
      print(f"파일 '{file}' 처리 중 오류 발생: {e}")

  merged_df.to_csv(output_csv, index=False, encoding='utf-8-sig')
  # merged_df.to_excel(OUTPUT_EXCEL, index=False, engine='openpyxl')  # 필요시 주석 해제
  print(f"모든 엑셀 파일을 병합하여 '{OUTPUT_CSV}'로 저장했습니다.")


if __name__ == "__main__":
  merge_excels_to_csv(FOLDER_PATH, OUTPUT_CSV)
