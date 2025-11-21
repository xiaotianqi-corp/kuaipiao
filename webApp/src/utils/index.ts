export const nameGenerator = (firstName: string, lastName: string) => {
  return `${firstName} ${lastName}`;
};

export const formatDate = (dateRaw: string) => {
  const date = new Date(dateRaw);

  const formattedDate = new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  }).format(date);
  return formattedDate;
};
