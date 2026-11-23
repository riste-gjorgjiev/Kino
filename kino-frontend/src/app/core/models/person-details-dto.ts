import { PersonCreditDto } from './person-credit-dto';

export interface PersonDetailsDto {
  id: number;
  name: string;
  biography: string | null;
  profileUrl: string | null;
  birthday: string | null;
  deathday: string | null;
  placeOfBirth: string | null;
  alsoKnownAs: string[];
  knownForDepartment: string | null;
  credits: PersonCreditDto[];
}
